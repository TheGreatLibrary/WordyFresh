package com.sinya.projects.wordle.presentation.dictionary

import com.sinya.projects.wordle.domain.model.DictionaryItem

sealed interface DictionaryUiState {
    data object Loading : DictionaryUiState
    
    data class Success(
        val searchQuery: String = "",
        val dictionaryList: List<DictionaryItem> = emptyList(),
        val isRefreshing: Boolean = false,
        val errorMessage: String? = null,
    ) : DictionaryUiState
}