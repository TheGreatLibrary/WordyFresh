package com.sinya.projects.wordle.screen.profile

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.data.local.database.AppDatabase
import com.sinya.projects.wordle.data.remote.supabase.SupabaseService
import com.sinya.projects.wordle.data.repository.AvatarRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val supabase: SupabaseClient,
    private val db: AppDatabase,
    private val avatarRepo: AvatarRepository
) : ViewModel() {

    private val _state = mutableStateOf<ProfileUiState>(ProfileUiState.Loading)
    val state: State<ProfileUiState> = _state

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

    fun onEvent(event: ProfileUiEvent) {
        when(event) {
            is ProfileUiEvent.SignOut -> signOut()
            is ProfileUiEvent.UpdateAvatar -> updateAvatar(event.uri)
            is ProfileUiEvent.LoadAvatar -> loadAvatar()
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val userId = supabase.auth.currentUserOrNull()?.id
            if (userId == null) {
                _state.value = ProfileUiState.NoAccount
                return@launch
            }

            try {
                val profile = SupabaseService.fetchProfile(userId)
                if (profile != null) {
                    _state.value = ProfileUiState.Success(
                        profile = profile,
                        email  = getEmail(),
                        avatarUri = null,
                        onEvent = ::onEvent)
                } else {
                    _state.value = ProfileUiState.NoAccount
                }
            } catch (e: Exception) {
                _state.value = ProfileUiState.Error(e.localizedMessage ?: "Ошибка загрузки профиля")
            }
        }
    }

    private fun signOut() {
        viewModelScope.launch {
            val userId = supabase.auth.currentUserOrNull()?.id ?: return@launch
            avatarRepo.deleteLocal(userId)
            db.clearAll()
            supabase.auth.signOut()
            _state.value = ProfileUiState.NoAccount
        }
    }

    private fun getEmail(): String {
        val user = supabase.auth.currentUserOrNull()
        return user?.email ?: "Данные не найдены"
    }

    private fun updateAvatar(uri: Uri) {
        viewModelScope.launch {
            val userId = supabase.auth.currentUserOrNull()?.id ?: return@launch
            val newAvatar = avatarRepo.uploadAvatar(userId, uri)
            val current = _state.value
            if (current is ProfileUiState.Success) {
                _state.value = current.copy(avatarUri = newAvatar)
            }
        }
    }

    private fun loadAvatar() {
        viewModelScope.launch {
            val userId = supabase.auth.currentUserOrNull()?.id ?: return@launch
            val avatar = avatarRepo.downloadAvatar(userId)
            val current = _state.value
            if (current is ProfileUiState.Success) {
                _state.value = current.copy(avatarUri = avatar)
            }
        }
    }
}

