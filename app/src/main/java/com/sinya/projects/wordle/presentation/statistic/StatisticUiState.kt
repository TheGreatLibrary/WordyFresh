package com.sinya.projects.wordle.presentation.statistic

import androidx.annotation.StringRes
import com.sinya.projects.wordle.data.local.database.entity.ModeStatisticsTranslations
import com.sinya.projects.wordle.domain.model.StatAggregated
import com.sinya.projects.wordle.domain.enums.GameMode

sealed interface StatisticUiState {
    data object Loading : StatisticUiState

    data class Success(
        val selectedMode: ModeStatisticsTranslations,
        val statisticList: List<StatAggregated> = emptyList(),
        val modes: List<ModeStatisticsTranslations> = emptyList(),
        val currentStatistic: StatAggregated = StatAggregated(GameMode.ALL.id),
        val isRefreshing: Boolean = false,
        @StringRes val errorMessage: Int? = null,
    ) : StatisticUiState

    data class Error(@StringRes val errorMessage: Int) : StatisticUiState
}