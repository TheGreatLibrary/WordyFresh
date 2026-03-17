package com.sinya.projects.wordle.presentation.statistic

import com.sinya.projects.wordle.domain.model.StatAggregated
import com.sinya.projects.wordle.domain.enums.GameMode

sealed interface StatisticUiState {
    data object Loading : StatisticUiState
    data class Success(
        val selectedMode: GameMode = GameMode.ALL,
        val statisticList: List<StatAggregated> = emptyList(),
        val currentStatistic: StatAggregated = StatAggregated(GameMode.ALL.id),
        val isRefreshing: Boolean = false,
        val errorMessage: String? = null,
    ) : StatisticUiState
}