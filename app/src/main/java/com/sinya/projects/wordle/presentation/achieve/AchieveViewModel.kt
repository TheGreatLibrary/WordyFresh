package com.sinya.projects.wordle.presentation.achieve

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.data.local.datastore.SettingsEngine
import com.sinya.projects.wordle.domain.useCase.ClearAllAchievementUseCase
import com.sinya.projects.wordle.domain.useCase.GetAllAchievementUseCase
import com.sinya.projects.wordle.domain.useCase.SyncAchievementUseCase
import com.sinya.projects.wordle.utils.getErrorMessage
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = AchieveViewModel.Factory::class)
class AchieveViewModel @AssistedInject constructor(
    @Assisted("id") private val id: Int?,
    private val getAllAchievementUseCase: GetAllAchievementUseCase,
    private val syncAchievementUseCase: SyncAchievementUseCase,
    private val clearAllAchievementUseCase: ClearAllAchievementUseCase,
    private val settingsEngine: SettingsEngine
) : ViewModel() {

    private val _state = MutableStateFlow<AchieveUiState>(AchieveUiState.Loading)
    val state = _state.asStateFlow()

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("id") id: Int?,
        ): AchieveViewModel
    }

    init {
        viewModelScope.launch {
            getAllAchievementUseCase(settingsEngine.uiState.value.language)
                .map { list ->
                    AchieveUiState.Success(
                        isRefreshing = false,
                        showAchieveDialog = list.firstOrNull { it.id == id },
                        achieveList = list.groupBy { it.categoryName }
                    )
                }
                .catch { emit(AchieveUiState.Success(errorMessage = it.getErrorMessage())) }
                .collect { _state.value = it }
        }
    }

    fun onEvent(event: AchieveEvent) {
        when (event) {
            AchieveEvent.OnRefresh -> refreshAchievement()

            AchieveEvent.OnClearAll -> clearAllAchievement()

            AchieveEvent.OnErrorShown -> updateIfSuccess { it.copy(errorMessage = null) }

            is AchieveEvent.VisibleDialog -> updateIfSuccess { it.copy(showAchieveDialog = event.item) }
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
                updateIfSuccess {
                    it.copy(
                        isRefreshing = false,
                        errorMessage = exception.getErrorMessage()
                    )
                }
            }
        )
    }

    private fun clearAllAchievement() = viewModelScope.launch {
        clearAllAchievementUseCase().onFailure { exception ->
            updateIfSuccess {
                it.copy(errorMessage = exception.getErrorMessage())
            }
        }
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