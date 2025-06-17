package com.sinya.projects.wordle.screen.achieve

import com.sinya.projects.wordle.domain.model.data.AchieveItem

sealed class AchieveUiState {
    data object Loading : AchieveUiState()
    data class Success(
        val achieveList: List<AchieveItem> = emptyList(),
        val isRefreshing: Boolean = false,
        val onEvent: (AchieveUiEvent) -> Unit
    ) : AchieveUiState()
    data class Error(val message: String) : AchieveUiState()
}

sealed class AchieveUiEvent {
    data object OnRefreshList : AchieveUiEvent()
}