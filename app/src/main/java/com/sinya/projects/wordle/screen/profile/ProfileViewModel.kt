package com.sinya.projects.wordle.screen.profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.data.local.database.AppDatabase
import com.sinya.projects.wordle.data.remote.supabase.SupabaseClientHolder
import com.sinya.projects.wordle.data.remote.supabase.SupabaseService
import com.sinya.projects.wordle.data.repository.AvatarRepository
import com.sinya.projects.wordle.domain.model.entity.Profiles
import com.sinya.projects.wordle.ui.components.ProfileUiState
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

class ProfileViewModel(
    private val supabase: SupabaseClient,
    private val db: AppDatabase,
    private val avatarRepo: AvatarRepository
) : ViewModel() {

    private val _uiState = mutableStateOf<ProfileUiState>(ProfileUiState.Loading)
    val uiState: State<ProfileUiState> = _uiState

    companion object {
        fun provideFactory(
            db: AppDatabase,
            supabase: SupabaseClient,
            avatarRepo: AvatarRepository
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProfileViewModel(supabase, db, avatarRepo) as T
                }
            }
        }
    }

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val userId = supabase.auth.currentUserOrNull()?.id
            if (userId == null) {
                _uiState.value = ProfileUiState.NoAccount
                return@launch
            }

            try {
                val profile = SupabaseService.fetchProfile(userId)
                if (profile != null) {
                    _uiState.value = ProfileUiState.Success(profile, null)
                } else {
                    _uiState.value = ProfileUiState.NoAccount
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.localizedMessage ?: "Ошибка загрузки профиля")
            }
        }
    }


    fun signOut() {
        viewModelScope.launch {
            val userId = supabase.auth.currentUserOrNull()?.id ?: return@launch
            avatarRepo.deleteLocal(userId)
            db.clearAll()
            supabase.auth.signOut()
            _uiState.value = ProfileUiState.NoAccount
        }
    }

    fun getEmail(): String {
        val user = supabase.auth.currentUserOrNull()
        return user?.email ?: "Данные не найдены"
    }

    fun updateAvatar(uri: Uri) {
        viewModelScope.launch {
            val userId = supabase.auth.currentUserOrNull()?.id ?: return@launch
            val newAvatar = avatarRepo.uploadAvatar(userId, uri)
            val current = _uiState.value
            if (current is ProfileUiState.Success) {
                _uiState.value = current.copy(avatarUri = newAvatar)
            }
        }
    }

    fun loadAvatar() {
        viewModelScope.launch {
            val userId = supabase.auth.currentUserOrNull()?.id ?: return@launch
            val avatar = avatarRepo.downloadAvatar(userId)
            val current = _uiState.value
            if (current is ProfileUiState.Success) {
                _uiState.value = current.copy(avatarUri = avatar)
            }
        }
    }
}

