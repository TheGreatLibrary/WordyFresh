package com.sinya.projects.wordle.data.local.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.NotFoundRestException
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.io.FileOutputStream

class AvatarRepository(
    private val supabase: SupabaseClient,
    private val context: Context
) {
    private val _avatarUriFlow = MutableStateFlow<Uri?>(null)
    val avatarUriFlow: StateFlow<Uri?> get() = _avatarUriFlow

    private fun getAvatarFileName(userId: String) = "avatar_$userId.webp"

    suspend fun uploadAvatar(userId: String, uri: Uri): Uri? {
        val fileName = getAvatarFileName(userId)
        val file = compressToWebP(uri, userId)
        val uploadFile = file.copyTo(File(context.cacheDir, fileName), overwrite = true)

        supabase.storage.from("avatars").upload(fileName, uploadFile) { upsert = true }

        val finalFile = uploadFile.copyTo(File(context.filesDir, fileName), overwrite = true)
        val finalUri = Uri.fromFile(finalFile)
        _avatarUriFlow.value = finalUri // 🔥 обновили flow

        return finalUri
    }

    suspend fun downloadAvatar(userId: String): Uri? {
        val fileName = getAvatarFileName(userId)
        val localFile = File(context.filesDir, fileName)

        if (localFile.exists()) {
            val localUri = Uri.fromFile(localFile)
            _avatarUriFlow.value = localUri // 🔥 локальное обновление
            return localUri
        }

        val downloaded = try {
            supabase.storage.from("avatars").downloadAuthenticated(fileName)
        } catch (_: NotFoundRestException) {
            null
        }

        return downloaded?.let {
            localFile.writeBytes(it)
            val uri = Uri.fromFile(localFile)
            _avatarUriFlow.value = uri // 🔥 загрузка и пуш
            uri
        }
    }

    fun deleteLocal(userId: String) {
        val file = File(context.filesDir, getAvatarFileName(userId))
        if (file.exists()) file.delete()
        _avatarUriFlow.value = null
    }

    private fun compressToWebP(uri: Uri, userId: String, width: Int? = 500, height: Int? = 500): File {
        val inputStream = context.contentResolver.openInputStream(uri) ?: error("Failed to open input")
        val original = BitmapFactory.decodeStream(inputStream)
        inputStream.close()

        // Вычисляем сторону квадрата (по меньшей из ширины/высоты)
        val side = minOf(original.width, original.height)

        // Центрированная обрезка
        val offsetX = (original.width - side) / 2
        val offsetY = (original.height - side) / 2

        val squared = Bitmap.createBitmap(original, offsetX, offsetY, side, side)

//        if (width != null && height!= null) {}
        val scaled = Bitmap.createScaledBitmap(squared, 500, 500, true)

        val file = File(context.filesDir, getAvatarFileName(userId))
        FileOutputStream(file).use {
            scaled.compress(Bitmap.CompressFormat.WEBP, 80, it)
        }

        return file
    }
}