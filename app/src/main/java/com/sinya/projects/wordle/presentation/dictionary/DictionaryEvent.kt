package com.sinya.projects.wordle.presentation.dictionary

import com.sinya.projects.wordle.domain.model.DictionaryItem
import com.sinya.projects.wordle.domain.enums.VibrationType
import com.sinya.projects.wordle.domain.model.ModeOfSorting
import com.sinya.projects.wordle.domain.model.SortParam

sealed interface DictionaryEvent {
    data object OnRefresh : DictionaryEvent
    data object OnErrorShown: DictionaryEvent
    data object OnClearAll : DictionaryEvent
    data class OnSearchQueryChanged(val query: String) : DictionaryEvent
    data class OnReloadedDefinition(val item: DictionaryItem) : DictionaryEvent
    data class OnVibrate(val type: VibrationType) : DictionaryEvent
    data class SortParamChange(val mode: ModeOfSorting, val onSelect: SortParam) : DictionaryEvent
}
