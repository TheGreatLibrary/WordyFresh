package com.sinya.projects.wordle.presentation.dictionary

import androidx.annotation.StringRes
import com.sinya.projects.wordle.domain.model.DictionaryItem
import com.sinya.projects.wordle.domain.model.ModeOfSorting

sealed interface DictionaryUiState {
    data object Loading : DictionaryUiState

    data class Success(
        val searchQuery: String = "",
        val dictionaryList: List<DictionaryItem> = emptyList(),
        val isRefreshing: Boolean = false,
        val modes: List<ModeOfSorting> = listOf(ModeOfSorting.Length(), ModeOfSorting.Language()),
        @StringRes val errorMessage: Int? = null,
    ) : DictionaryUiState
}