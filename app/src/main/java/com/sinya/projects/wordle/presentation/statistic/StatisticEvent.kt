package com.sinya.projects.wordle.presentation.statistic

import com.sinya.projects.wordle.data.local.database.entity.ModeStatisticsTranslations

sealed interface StatisticEvent {
    data class SelectMode(val mode: ModeStatisticsTranslations) : StatisticEvent
    data object OnErrorShown : StatisticEvent
    data object OnClearAll : StatisticEvent
    data object OnRefresh: StatisticEvent
}