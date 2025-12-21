package com.sinya.projects.wordle.presentation.statistic

import com.sinya.projects.wordle.data.local.database.entity.OfflineStatistic
import com.sinya.projects.wordle.domain.enums.GameMode

sealed interface StatisticUiState {
    data object Loading : StatisticUiState
    data class Success(
        val selectedMode: GameMode = GameMode.ALL,
        val statisticList: List<OfflineStatistic> = emptyList(),
        val currentStatistic: OfflineStatistic = OfflineStatistic(GameMode.ALL.id),
        val isRefreshing: Boolean = false,
        val errorMessage: String? = null,
    ) : StatisticUiState
}