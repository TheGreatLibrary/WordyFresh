package com.sinya.projects.wordle.presentation.statistic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.data.local.database.entity.ModeStatisticsTranslations
import com.sinya.projects.wordle.data.local.datastore.SettingsEngine
import com.sinya.projects.wordle.domain.enums.GameMode
import com.sinya.projects.wordle.domain.useCase.ClearAllStatisticsUseCase
import com.sinya.projects.wordle.domain.useCase.GetAllStatisticsUseCase
import com.sinya.projects.wordle.domain.useCase.GetModesUseCase
import com.sinya.projects.wordle.domain.useCase.SyncStatisticUseCase
import com.sinya.projects.wordle.utils.getErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class StatisticViewModel @Inject constructor(
    private val getAllStatisticsUseCase: GetAllStatisticsUseCase,
    private val syncStatisticUseCase: SyncStatisticUseCase,
    private val getModesUseCase: GetModesUseCase,
    private val settingsEngine: SettingsEngine,
    private val clearAllStatisticsUseCase: ClearAllStatisticsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<StatisticUiState>(StatisticUiState.Loading)
    val state: StateFlow<StatisticUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            loadStatistic()
        }
    }

    fun onEvent(event: StatisticEvent) {
        when (event) {
            StatisticEvent.OnRefresh -> refreshStatistics()

            StatisticEvent.OnClearAll -> clearAllStatistics()

            StatisticEvent.OnErrorShown -> updateIfSuccess {
                it.copy(errorMessage = null)
            }

            is StatisticEvent.SelectMode -> selectMode(event.mode)
        }
    }

    private fun selectMode(mode: ModeStatisticsTranslations) = updateIfSuccess { success ->
        success.copy(
            selectedMode = mode,
            currentStatistic = success.statisticList.first { it.modeId == mode.modeId }
        )
    }

    private suspend fun loadStatistic() {
        val lang = settingsEngine.uiState.value.language

        val statsResult = getAllStatisticsUseCase()
        val modesResult = getModesUseCase(lang)

        if (statsResult.isFailure || modesResult.isFailure) {
            _state.value = StatisticUiState.Error(
                errorMessage = (statsResult.exceptionOrNull() ?: Exception()).getErrorMessage()
            )
            return
        }

        val stats = statsResult.getOrThrow()
        val modes = modesResult.getOrThrow()

        _state.value = StatisticUiState.Success(
            statisticList = stats,
            modes = modes,
            currentStatistic = stats.first { it.modeId == GameMode.ALL.id },
            selectedMode = modes.first { it.modeId == GameMode.ALL.id }
        )
    }

    private fun refreshStatistics() {
        updateIfSuccess {
            it.copy(isRefreshing = true)
        }

        viewModelScope.launch {
            syncStatisticUseCase().fold(
                onSuccess = {
                    loadStatistic()
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
    }

    private fun clearAllStatistics() = viewModelScope.launch {
        clearAllStatisticsUseCase().onSuccess { loadStatistic() }
    }

    private fun updateIfSuccess(transform: (StatisticUiState.Success) -> StatisticUiState.Success) {
        _state.update { currentState ->
            if (currentState is StatisticUiState.Success) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }
}



