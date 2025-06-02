package com.sinya.projects.wordle.screen.statistic

import com.sinya.projects.wordle.domain.model.entity.OfflineStatistic

data class StatisticUi(
    val loadingState: StatisticState = StatisticState.Loading,
    val selectedMode: String = "",
    val statisticList: List<OfflineStatistic> = emptyList()
)

sealed class StatisticUiEvent {
    data class SelectMode(val modeId: String) : StatisticUiEvent()
    data object Reload : StatisticUiEvent()
}

sealed class StatisticState {
    data object Loading : StatisticState()
    data class Success(val data: List<OfflineStatistic>) : StatisticState()
    data object Error : StatisticState()
}

sealed class StatisticTypeContainer {
    data class Count(val value: Int) : StatisticTypeContainer()
    data class Time(val value: String) : StatisticTypeContainer()
    data class Percent(
        val value: Float,
        val statisticByMode: OfflineStatistic
    ) : StatisticTypeContainer()
}