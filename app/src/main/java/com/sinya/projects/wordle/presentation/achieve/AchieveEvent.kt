package com.sinya.projects.wordle.presentation.achieve

sealed interface AchieveEvent {
    data object OnRefresh : AchieveEvent
    data object OnClearAll : AchieveEvent
    data object OnErrorShown : AchieveEvent
}