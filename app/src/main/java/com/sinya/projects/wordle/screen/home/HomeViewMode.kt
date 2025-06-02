package com.sinya.projects.wordle.screen.home

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.data.local.datastore.AppDataStore
import com.sinya.projects.wordle.data.repository.AvatarRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

class HomeViewModel(
    private val supabase: SupabaseClient,
    private val avatarRepo: AvatarRepository

) : ViewModel() {

    var uiState = mutableStateOf(HomeUi())
        private set

    companion object {
        fun provideFactory(
            supabase: SupabaseClient,
            avatarRepo: AvatarRepository
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(supabase, avatarRepo) as T
                }
            }
        }
    }

    fun loadAvatar() {
        viewModelScope.launch {
            val userId = supabase.auth.currentUserOrNull()?.id ?: return@launch
            val avatar = avatarRepo.downloadAvatar(userId)
            uiState.value = uiState.value.copy(avatarUri = avatar)
        }
    }

    fun loadSaveGame(context: Context) {
        viewModelScope.launch {
            val savedGame = AppDataStore.loadGame(context)
            uiState.value = uiState.value.copy(savedGame = savedGame)
        }
    }

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.FriendDialogUploadVisible -> uiState.value =
                uiState.value.copy(showFriendDialog = event.visibility)

            is HomeUiEvent.BottomSheetUploadMode -> uiState.value = uiState.value.copy(
                showBottomSheet = true,
                modeGame = event.mode
            )

            is HomeUiEvent.BottomSheetUploadVisible -> uiState.value =
                uiState.value.copy(showBottomSheet = event.visibility)
        }
    }
}