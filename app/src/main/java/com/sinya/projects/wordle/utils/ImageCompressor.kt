package com.sinya.projects.wordle.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import com.sinya.projects.wordle.data.remote.web.LegalLinks.getAvatarFileName
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.io.File
import java.io.FileOutputStream

interface ImageCompressor {
    fun compressToSquareWebP(uri: Uri, userId: String, size: Int = 500): File
}

@Singleton
class BitmapImageCompressor @Inject constructor(
    @ApplicationContext private val context: Context
) : ImageCompressor {

    override fun compressToSquareWebP(uri: Uri, userId: String, size: Int): File {
        val inputStream = context.contentResolver.openInputStream(uri) ?: error("Failed to open input")
        val original = BitmapFactory.decodeStream(inputStream)
        inputStream.close()

        val side = minOf(original.width, original.height)
        val offsetX = (original.width - side) / 2
        val offsetY = (original.height - side) / 2

        val squared = Bitmap.createBitmap(original, offsetX, offsetY, side, side)
        val scaled = Bitmap.createScaledBitmap(squared, size, size, true)

        val file = File(context.cacheDir, getAvatarFileName(userId))
        FileOutputStream(file).use {
            scaled.compress(webpFormat(), 80, it)
        }

        return file
    }

    private fun webpFormat(): Bitmap.CompressFormat =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Bitmap.CompressFormat.WEBP_LOSSY
        else @Suppress("DEPRECATION") Bitmap.CompressFormat.WEBP
}