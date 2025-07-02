package com.sinya.projects.wordle.screen.home

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.data.local.achievement.AchievementTrigger
import com.sinya.projects.wordle.data.local.achievement.objects.AchievementManager
import com.sinya.projects.wordle.data.local.database.AppDatabase
import com.sinya.projects.wordle.data.local.datastore.AppDataStore
import com.sinya.projects.wordle.data.local.repository.AvatarRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

class HomeViewModel(
    private val supabase: SupabaseClient,
    private val avatarRepo: AvatarRepository,
    private val context: Context,
    private val db: AppDatabase
) : ViewModel() {

    private val _state = mutableStateOf<HomeUiState>(HomeUiState.Loading)
    val state: State<HomeUiState> = _state

    companion object {
        fun provideFactory(
            supabase: SupabaseClient,
            avatarRepo: AvatarRepository,
            context: Context,
            db: AppDatabase
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(supabase, avatarRepo, context, db) as T
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            val userId = supabase.auth.currentUserOrNull()?.id
            if (userId != null) {
                avatarRepo.downloadAvatar(userId) // первая загрузка
                launch {
                    avatarRepo.avatarUriFlow.collect { newUri ->
                        val current = _state.value
                        if (current is HomeUiState.Success) {
                            _state.value = current.copy(avatarUri = newUri)
                        }
                    }
                }
            }

            AppDataStore.getSavedGame(context).collect { savedGame ->
                val current = _state.value
                if (current is HomeUiState.Success) {
                    _state.value = current.copy(savedGame = savedGame)
                } else {
                    _state.value = HomeUiState.Success(
                        avatarUri = avatarRepo.avatarUriFlow.value,
                        savedGame = savedGame,
                        onEvent = ::onEvent
                    )
                }
            }
        }
    }

    fun onEvent(event: HomeUiEvent) {
        val currentState = _state.value
        if (currentState !is HomeUiState.Success) return

        when (event) {
            is HomeUiEvent.FriendDialogUploadVisible -> {
                _state.value = currentState.copy(showFriendDialog = event.visibility)
            }
            is HomeUiEvent.BottomSheetUploadMode -> {
                _state.value = currentState.copy(
                    showBottomSheet = true,
                    modeGame = event.mode
                )
            }
            is HomeUiEvent.BottomSheetUploadVisible -> {
                _state.value = currentState.copy(showBottomSheet = event.visibility)
            }
            is HomeUiEvent.SendEmailSupport -> {
                viewModelScope.launch {
                    AchievementManager.onTrigger(AchievementTrigger.SupportMessageSent, db.loadStats())
                }
            }
        }
    }
}