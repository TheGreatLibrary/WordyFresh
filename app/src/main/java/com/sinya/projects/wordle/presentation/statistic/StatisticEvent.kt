package com.sinya.projects.wordle.presentation.statistic

import com.sinya.projects.wordle.domain.enums.GameMode

sealed interface StatisticEvent {
    data class SelectMode(val mode: GameMode) : StatisticEvent
    data object OnErrorShown : StatisticEvent
    data object OnClearAll : StatisticEvent
    data object OnRefresh: StatisticEvent
}