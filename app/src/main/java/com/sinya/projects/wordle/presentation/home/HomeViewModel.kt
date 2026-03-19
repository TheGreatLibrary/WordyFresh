package com.sinya.projects.wordle.presentation.home

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.data.local.achievement.AchievementTrigger
import com.sinya.projects.wordle.data.local.datastore.SavedGameState
import com.sinya.projects.wordle.data.local.datastore.SettingsEngine
import com.sinya.projects.wordle.data.remote.supabase.SessionManager
import com.sinya.projects.wordle.domain.enums.GameMode
import com.sinya.projects.wordle.domain.useCase.CheckAchievementUseCase
import com.sinya.projects.wordle.domain.useCase.GetDataWordUseCase
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.utils.decode
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val settingsEngine: SettingsEngine,
    private val sessionManager: SessionManager,
    private val getWordUseCase: GetDataWordUseCase,
    private val checkAchievementUseCase: CheckAchievementUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        observeChanges()
    }

    fun handleDeepLink(intent: Intent?) {
        if (intent?.data != null) {
            val word = decode((intent.takeIf { it.action == Intent.ACTION_VIEW }
                ?.data?.getQueryParameter("word") ?: "").trim())?.uppercase() ?: ""

            viewModelScope.launch {
                getWordUseCase(word).fold(
                    onSuccess = { wordData ->
                        _state.value = HomeUiState.Invite(
                            ScreenRoute.Game(
                                mode = GameMode.FRIENDLY.id,
                                wordLength = wordData.length,
                                lang = wordData.language,
                                word = wordData.word
                            )
                        )
                    },
                    onFailure = {
                        updateIfSuccess {
                            it.copy(errorMessage = "Ошибка получения слова!")
                        }
                    }
                )
            }
        }
    }

    private fun observeChanges() = viewModelScope.launch {
        combine(
            sessionManager.avatar,
            settingsEngine.uiState.map { it.lastGame }
        ) { avatarUri, lastGame ->
            HomeUiState.Success(
                avatarUri = avatarUri,
                savedGame = (lastGame as? SavedGameState.Loaded)?.game
            )
        }.collect { newState ->
            _state.update { newState }
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
        checkAchievementUseCase(
            AchievementTrigger.SupportMessageSent,
            settingsEngine.uiState.value.language
        ).fold(
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