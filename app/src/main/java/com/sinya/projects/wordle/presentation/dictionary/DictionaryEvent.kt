package com.sinya.projects.wordle.presentation.dictionary

import com.sinya.projects.wordle.domain.model.DictionaryItem

sealed interface DictionaryEvent {
    data object OnRefresh : DictionaryEvent
    data object OnErrorShown: DictionaryEvent
    data object OnClearAll : DictionaryEvent
    data class OnSearchQueryChanged(val query: String) : DictionaryEvent
    data class OnReloadedDefinition(val item: DictionaryItem) : DictionaryEvent
}
