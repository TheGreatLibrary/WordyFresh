package com.sinya.projects.wordle.screen.profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.data.local.database.AppDatabase
import com.sinya.projects.wordle.domain.model.entity.Profiles
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.exceptions.NotFoundRestException
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class ProfileViewModel(private val supabase: SupabaseClient, private val db: AppDatabase) : ViewModel() {
    var profile by mutableStateOf<Profiles?>(null)
    var error by mutableStateOf<String?>(null)
    var avatarUri by mutableStateOf<Uri?>(null)
        private set

    fun updateAvatar(uri: Uri) {
        avatarUri = uri
    }

    init {
        fetchProfile()
    }

    companion object {
        fun provideFactory(
            db: AppDatabase,
            supabase: SupabaseClient
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProfileViewModel(supabase, db) as T
                }
            }
        }
    }

    private fun fetchProfile() {
        viewModelScope.launch {
            try {
                val userId = supabase.auth.currentUserOrNull()?.id
                    ?: throw Exception("Авторизируйтесь, чтобы увидеть увидеть данные о пользователе")
                profile = supabase
                    .from("profiles")
                    .select(columns = Columns.list("*")) {
                        filter {
                            eq("id", userId)
                        }
                    }
                    .decodeSingle<Profiles>()
            } catch (e: Exception) {
                error = e.localizedMessage ?: "Ошибка загрузки профиля"
            }
        }
    }

    fun getEmail() : String {
        val user = supabase.auth.currentUserOrNull()
        return user?.email ?: "Данные не найдены"
    }

    fun updateAvatar(context: Context, uri: Uri) {
        viewModelScope.launch {
            val userId = supabase.auth.currentUserOrNull()?.id ?: return@launch
            val fileName = "avatar_$userId.webp"
            val file = compressToWebP(context, uri, userId)
            val uploadFile = file.copyTo(File(context.cacheDir, fileName), overwrite = true)

            val bucket = supabase.storage.from("avatars")
            bucket.upload(fileName, uploadFile) {
                upsert = true
            }

            // 3. Сохраняем локально
            val localFile = File(context.filesDir, fileName)
            uploadFile.copyTo(localFile, overwrite = true)

            avatarUri = Uri.fromFile(localFile)

            // 4. Сохраняем путь в таблице profile (если нужно)
            supabase.from("profiles").update(
                mapOf("avatar_url" to fileName)
            ) {
                filter {
                    eq("id", userId)
                }
            }
        }
    }

    private fun compressToWebP(context: Context, uri: Uri, userId: String): File {
        val inputStream = context.contentResolver.openInputStream(uri) ?: error("Failed to open input")
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()

        val resized = Bitmap.createScaledBitmap(bitmap, 500, 500, true)
        val file = File(context.filesDir, "avatar_$userId.webp") // постоянный путь
        val out = FileOutputStream(file)
        resized.compress(Bitmap.CompressFormat.WEBP, 80, out)
        out.close()
        return file
    }

    private fun deleteLocalAvatar(context: Context) {
        val userId = supabase.auth.currentUserOrNull()?.id ?: return
        val file = File(context.filesDir, "avatar_$userId.webp")
        if (file.exists()) file.delete()
    }

    fun signOut(context: Context) {
        viewModelScope.launch {
            deleteLocalAvatar(context)
            db.clearAll()
            supabase.auth.signOut()
            profile=null
        }
    }

    fun loadAvatar(context: Context) {
        viewModelScope.launch {
            val userId = supabase.auth.currentUserOrNull()?.id ?: return@launch
            val fileName = "avatar_$userId.webp"
            val localFile = File(context.filesDir, fileName)

            if (localFile.exists()) {
                avatarUri = Uri.fromFile(localFile)
                Log.d("ПИЗДА", localFile.exists().toString())
            } else {
                val avatarFile = try {
                    supabase.storage.from("avatars")
                    .downloadAuthenticated(fileName)
                } catch (e: NotFoundRestException) {
                    null // файла нет — возвращаем null или дефолтную картинку
                }
                Log.d("ПИЗДА", "avatarFile = ${userId}")

                if (avatarFile != null) {
                    localFile.writeBytes(avatarFile)
                    avatarUri = Uri.fromFile(localFile)
                }
            }
        }
    }
}

