package com.sinya.projects.wordle.presentation.statistic

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.domain.enums.GameMode
import com.sinya.projects.wordle.domain.error.UserNotAuthenticatedException
import com.sinya.projects.wordle.domain.useCase.ClearAllStatisticsUseCase
import com.sinya.projects.wordle.domain.useCase.GetAllStatisticsUseCase
import com.sinya.projects.wordle.domain.useCase.SyncStatisticUseCase
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

            StatisticEvent.OnErrorShown -> onErrorShown()

            is StatisticEvent.SelectMode -> selectMode(event.mode)
        }
    }

    private fun onErrorShown() = updateIfSuccess {
        it.copy(errorMessage = null)
    }

    private fun selectMode(mode: GameMode) = updateIfSuccess { success ->
        success.copy(
            selectedMode = mode,
            currentStatistic = success.statisticList.first { it.modeId == mode.id }
        )
    }

    private suspend fun loadStatistic() {
        getAllStatisticsUseCase().fold(
            onSuccess = { stats ->
                _state.value = StatisticUiState.Success(
                    statisticList = stats,
                    currentStatistic = stats.first { it.modeId == GameMode.ALL.id }
                )
            },
            onFailure = { throwable ->
                Log.d("Error", throwable.toString())
                _state.value = StatisticUiState.Success(
                    errorMessage = throwable.message,
                )
            }
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
    }

    private fun clearAllStatistics() = viewModelScope.launch {
        clearAllStatisticsUseCase().fold(
            onSuccess = {
                loadStatistic()
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



