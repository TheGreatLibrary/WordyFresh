package com.sinya.projects.wordle.domain.source

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.sinya.projects.wordle.data.remote.web.LegalLinks.getAvatarFileName
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.io.File
import java.io.FileOutputStream

interface AvatarLocalDataSource {
    fun saveAvatar(userId: String, data: ByteArray): Uri
    fun getLocalAvatar(userId: String): Uri?
    fun deleteAvatar(userId: String)
}

@Singleton
class FileAvatarLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) : AvatarLocalDataSource {

    private fun getAvatarFile(userId: String) =
        File(context.filesDir, getAvatarFileName(userId))

    override fun saveAvatar(userId: String, data: ByteArray): Uri {
        val file = getAvatarFile(userId)
        file.writeBytes(data)
        return Uri.fromFile(file)
    }

    override fun getLocalAvatar(userId: String): Uri? {
        val file = getAvatarFile(userId)
        return if (file.exists()) Uri.fromFile(file) else null
    }

    override fun deleteAvatar(userId: String) {
        getAvatarFile(userId).delete()
    }
}

interface ImageCompressor {
    fun compressToSquareWebP(uri: Uri, userId: String, size: Int = 500): File
}

@Singleton
class BitmapImageCompressor @Inject constructor(
    @ApplicationContext private val context: Context
) : ImageCompressor {

    @RequiresApi(Build.VERSION_CODES.R)
    override fun compressToSquareWebP(uri: Uri, userId: String, size: Int): File {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: error("Failed to open input")
        val original = BitmapFactory.decodeStream(inputStream)
        inputStream.close()

        val side = minOf(original.width, original.height)
        val offsetX = (original.width - side) / 2
        val offsetY = (original.height - side) / 2

        val squared = Bitmap.createBitmap(original, offsetX, offsetY, side, side)
        val scaled = Bitmap.createScaledBitmap(squared, size, size, true)

        val file = File(context.cacheDir, getAvatarFileName(userId))
        FileOutputStream(file).use {
            scaled.compress(Bitmap.CompressFormat.WEBP_LOSSY, 80, it)
        }

        return file
    }
}