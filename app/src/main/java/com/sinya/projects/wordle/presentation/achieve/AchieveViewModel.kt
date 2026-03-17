package com.sinya.projects.wordle.presentation.achieve

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.data.local.datastore.SettingsEngine
import com.sinya.projects.wordle.domain.error.UserNotAuthenticatedException
import com.sinya.projects.wordle.domain.useCase.ClearAllAchievementUseCase
import com.sinya.projects.wordle.domain.useCase.GetAllAchievementUseCase
import com.sinya.projects.wordle.domain.useCase.SyncAchievementUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AchieveViewModel @Inject constructor(
    private val getAllAchievementUseCase: GetAllAchievementUseCase,
    private val syncAchievementUseCase: SyncAchievementUseCase,
    private val clearAllAchievementUseCase: ClearAllAchievementUseCase,
    private val settingsEngine: SettingsEngine
) : ViewModel() {

    private val _state = MutableStateFlow<AchieveUiState>(AchieveUiState.Loading)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getAllAchievementUseCase(settingsEngine.uiState.value.language)
                .map { list ->
                    AchieveUiState.Success(
                        isRefreshing = false,
                        achieveList = list.groupBy { it.categoryName }
                    )
                }
                .catch { emit(AchieveUiState.Success(errorMessage = it.toString())) }
                .collect { _state.value = it }
        }
    }

    fun onEvent(event: AchieveEvent) {
        when (event) {
            AchieveEvent.OnRefresh -> refreshAchievement()

            AchieveEvent.OnClearAll -> clearAllAchievement()

            AchieveEvent.OnErrorShown -> onErrorShown()
        }
    }

    private fun onErrorShown() {
        _state.update { currentState ->
            if (currentState is AchieveUiState.Success) {
                currentState.copy(errorMessage = null)
            } else {
                currentState
            }
        }
    }

    private fun refreshAchievement() = viewModelScope.launch {
        updateIfSuccess {
            it.copy(isRefreshing = true)
        }

        syncAchievementUseCase().fold(
            onSuccess = {
                updateIfSuccess {
                    it.copy(isRefreshing = false)
                }
            },
            onFailure = { exception ->
                val errorMessage = when (exception) {
                    is UserNotAuthenticatedException -> "Требуется авторизация"
                    else -> "Ошибка синхронизации: ${exception.message}"
                }

                updateIfSuccess {
                    it.copy(
                        isRefreshing = false,
                        errorMessage = errorMessage
                    )
                }
            }
        )
    }

    private fun clearAllAchievement() = viewModelScope.launch {
        clearAllAchievementUseCase().fold(
            onSuccess = {
            },
            onFailure = { exception ->
                updateIfSuccess {
                    it.copy(
                        errorMessage = "Ошибка очистки: ${exception.message}"
                    )
                }
            }
        )
    }

    private fun updateIfSuccess(transform: (AchieveUiState.Success) -> AchieveUiState.Success) {
        _state.update { currentState ->
            if (currentState is AchieveUiState.Success) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }
}