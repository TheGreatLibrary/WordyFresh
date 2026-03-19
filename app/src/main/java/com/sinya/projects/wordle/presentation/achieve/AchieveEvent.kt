package com.sinya.projects.wordle.presentation.achieve

import com.sinya.projects.wordle.domain.model.AchieveItem

sealed interface AchieveEvent {
    data class VisibleDialog(val item: AchieveItem?) : AchieveEvent
    data object OnRefresh : AchieveEvent
    data object OnClearAll : AchieveEvent
    data object OnErrorShown : AchieveEvent
}