package com.sinya.projects.wordle.screen.statistic

import com.sinya.projects.wordle.data.local.entity.OfflineStatistic

sealed class StatisticUiState {
    data object Loading : StatisticUiState()
    data class Success(
        val selectedMode: Int = AppStatsModes.supported[0].id,
        val statisticList: List<OfflineStatistic> = emptyList(),
        val onEvent: (StatisticUiEvent) -> Unit
    ) : StatisticUiState()
    data class Error(val message: String) : StatisticUiState()
}

sealed class StatisticUiEvent {
    data class SelectMode(val modeId: Int) : StatisticUiEvent()
    data object Reload : StatisticUiEvent()
}

sealed class StatisticTypeContainer {
    data class Count(val value: Int) : StatisticTypeContainer()
    data class Time(val value: String) : StatisticTypeContainer()
    data class Percent(
        val value: Float,
        val statisticByMode: OfflineStatistic
    ) : StatisticTypeContainer()
}