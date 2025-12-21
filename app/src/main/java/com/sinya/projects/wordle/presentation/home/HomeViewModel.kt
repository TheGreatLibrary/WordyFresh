package com.sinya.projects.wordle.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.data.local.achievement.AchievementTrigger
import com.sinya.projects.wordle.data.local.datastore.DataStoreManager
import com.sinya.projects.wordle.domain.source.SupabaseAuthDataSource
import com.sinya.projects.wordle.domain.useCase.CheckAchievementUseCase
import com.sinya.projects.wordle.domain.useCase.GetAvatarUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authDataSource: SupabaseAuthDataSource,
    private val getAvatarUseCase: GetAvatarUseCase,
    private val checkAchievementUseCase: CheckAchievementUseCase,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _state = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        loadInitialData()
        observeAvatarChanges()
        observeSavedGameChanges()
    }

    private fun loadInitialData() = viewModelScope.launch {
        val userId = authDataSource.getCurrentUser()?.id

        if (userId != null) {
            getAvatarUseCase(userId)
        }

        _state.update {
            HomeUiState.Success(
                avatarUri = getAvatarUseCase.observeAvatar().value,
                savedGame = dataStoreManager.getSavedGame().first()
            )
        }
    }

    private fun observeAvatarChanges() = viewModelScope.launch {
        getAvatarUseCase.observeAvatar().collect { newUri ->
            _state.update { currentState ->
                if (currentState is HomeUiState.Success) {
                    currentState.copy(avatarUri = newUri)
                } else {
                    currentState
                }
            }
        }
    }

    private fun observeSavedGameChanges() = viewModelScope.launch {
        dataStoreManager.getSavedGame().collect { game ->
            _state.update { currentState ->
                if (currentState is HomeUiState.Success) {
                    currentState.copy(savedGame = game)
                } else {
                    currentState
                }
            }
        }
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.FriendDialogUploadVisible -> {
                updateIfSuccess { it.copy(showFriendBottomSheet = event.visibility) }
            }

            is HomeEvent.BottomSheetUploadMode -> {
                updateIfSuccess {
                    it.copy(
                        showGameBottomSheet = true,
                        modeGame = event.mode
                    )
                }
            }

            is HomeEvent.BottomSheetUploadVisible -> {
                updateIfSuccess { it.copy(showGameBottomSheet = event.visibility) }
            }

            HomeEvent.SendEmailSupport -> {
                sendSupportEmail()
            }

            HomeEvent.OnErrorShown -> {
                updateIfSuccess { it.copy(errorMessage = null) }
            }
        }
    }

    private fun sendSupportEmail() = viewModelScope.launch {
        checkAchievementUseCase(AchievementTrigger.SupportMessageSent).fold(
            onSuccess = {
                updateIfSuccess {
                    it.copy(errorMessage = "Письмо отправлено!")
                }
            },
            onFailure = { error ->
                updateIfSuccess {
                    it.copy(errorMessage = "Ошибка: ${error.message}")
                }
            }
        )
    }

    private fun updateIfSuccess(transform: (HomeUiState.Success) -> HomeUiState.Success) {
        _state.update { currentState ->
            if (currentState is HomeUiState.Success) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }
}