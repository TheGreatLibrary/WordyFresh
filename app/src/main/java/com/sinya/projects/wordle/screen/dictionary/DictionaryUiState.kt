package com.sinya.projects.wordle.screen.dictionary

import android.content.Context

sealed class DictionaryUiState {
    data object Loading : DictionaryUiState()
    data class Success(
        val searchQuery: String = "",
        val dictionaryList: List<DictionaryItem> = emptyList(),
        val isRefreshing: Boolean = false,
        val onEvent: (DictionaryUiEvent) -> Unit
    ) : DictionaryUiState()

    data class Error(val message: String) : DictionaryUiState()
}

sealed class DictionaryUiEvent {
    data object OnRefreshList : DictionaryUiEvent()
    data class OnSearchQueryChanged(val query: String) : DictionaryUiEvent()
    data class OnReloadedDefinition(val word: String, val context: Context) : DictionaryUiEvent()
    data class OnShareWord(val text: String, val context: Context) : DictionaryUiEvent()
    data class OnNavigateToInternetDictionary(val word: String, val context: Context) : DictionaryUiEvent()
}