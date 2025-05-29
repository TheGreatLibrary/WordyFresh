package com.sinya.projects.wordle.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.NotFoundRestException
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import java.io.File
import java.io.FileOutputStream

class AvatarRepository(
    private val supabase: SupabaseClient,
    private val context: Context
) {

    private fun getAvatarFileName(userId: String) = "avatar_$userId.webp"

    suspend fun uploadAvatar(userId: String, uri: Uri): Uri? {
        val fileName = getAvatarFileName(userId)
        val file = compressToWebP(uri, userId)
        val uploadFile = file.copyTo(File(context.cacheDir, fileName), overwrite = true)

        val bucket = supabase.storage.from("avatars")
        bucket.upload(fileName, uploadFile) {
            upsert = true
        }

        uploadFile.copyTo(File(context.filesDir, fileName), overwrite = true)
        return Uri.fromFile(File(context.filesDir, fileName))
    }

    suspend fun downloadAvatar(userId: String): Uri? {
        val fileName = getAvatarFileName(userId)
        val localFile = File(context.filesDir, fileName)

        if (localFile.exists()) return Uri.fromFile(localFile)

        val downloaded = try {
            supabase.storage.from("avatars").downloadAuthenticated(fileName)
        } catch (_: NotFoundRestException) {
            null
        }

        return downloaded?.let {
            localFile.writeBytes(it)
            Uri.fromFile(localFile)
        }
    }

    fun deleteLocal(userId: String) {
        val file = File(context.filesDir, getAvatarFileName(userId))
        if (file.exists()) file.delete()
    }

    private fun compressToWebP(uri: Uri, userId: String): File {
        val inputStream = context.contentResolver.openInputStream(uri) ?: error("Failed to open input")
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()

        val resized = Bitmap.createScaledBitmap(bitmap, 500, 500, true)
        val file = File(context.filesDir, getAvatarFileName(userId))
        FileOutputStream(file).use {
            resized.compress(Bitmap.CompressFormat.WEBP, 80, it)
        }
        return file
    }
}