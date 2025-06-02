package com.sinya.projects.wordle.screen.dictionary

import android.content.Context
import com.sinya.projects.wordle.domain.model.data.DictionaryItem

data class DictionaryUi(
    val searchQuery: String = "",
    val dictionaryList: List<DictionaryItem> = emptyList(),
    val isRefreshing: Boolean = false
)

sealed class DictionaryUiEvent {
    data object OnRefreshList : DictionaryUiEvent()
    data class OnSearchQueryChanged(val query: String) : DictionaryUiEvent()
    data class OnReloadedDefinition(val word: String, val context: Context) : DictionaryUiEvent()
    data class OnShareWord(val text: String, val context: Context) : DictionaryUiEvent()
    data class OnNavigateToInternetDictionary(val word: String, val context: Context) : DictionaryUiEvent()
}