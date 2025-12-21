package com.sinya.projects.wordle.presentation.achieve

import com.sinya.projects.wordle.domain.model.AchieveItem

sealed interface AchieveUiState {
    data object Loading : AchieveUiState

    data class Success(
        val achieveList: Map<String, List<AchieveItem>> = emptyMap(),
        val isRefreshing: Boolean = false,
        val errorMessage: String? = null
    ) : AchieveUiState
}

