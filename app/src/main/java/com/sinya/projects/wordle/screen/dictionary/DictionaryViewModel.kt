package com.sinya.projects.wordle.screen.dictionary

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.data.local.database.AppDatabase
import com.sinya.projects.wordle.domain.model.data.DictionaryItem
import com.sinya.projects.wordle.screen.home.HomeUiState
import com.sinya.projects.wordle.utils.getDefinitionWithFallback
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DictionaryViewModel(
    private val db: AppDatabase,
) : ViewModel() {

    private val _state = mutableStateOf<DictionaryUiState>(DictionaryUiState.Loading)
    val state: State<DictionaryUiState> = _state

    companion object {
        fun provideFactory(
            db: AppDatabase,
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DictionaryViewModel(db) as T
                }
            }
        }
    }

    init {
        loadDictionary()
    }

    fun onEvent(event: DictionaryUiEvent) {
        val currentState = _state.value
        if (currentState !is DictionaryUiState.Success) return

        when(event) {
            is DictionaryUiEvent.OnRefreshList -> {
                viewModelScope.launch {
                    _state.value = currentState.copy(isRefreshing = true)
                    loadDictionary()
                    _state.value = currentState.copy(isRefreshing = false)
                }
            }
            is DictionaryUiEvent.OnReloadedDefinition -> {
                viewModelScope.launch {
                    val description = getDefinitionWithFallback(event.word, event.context)
                    Log.d("database", description)
                    db.offlineDictionaryDao().insertOrUpdateDescription(db.wordDao().getWordId(event.word), description)
                    loadDictionary()
                }
            }
            is DictionaryUiEvent.OnSearchQueryChanged -> {
                _state.value = currentState.copy(searchQuery = event.query)
            }
            is DictionaryUiEvent.OnShareWord -> {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, event.text)
                }
                event.context.startActivity(
                    Intent.createChooser(
                        intent,
                        event.context.getString(R.string.shared_to)
                    )
                )
            }
            is DictionaryUiEvent.OnNavigateToInternetDictionary -> {
                val url = "https://academic.ru/searchall.php?SWord=${event.word}"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                event.context.startActivity(intent)
            }
        }
    }

    private fun loadDictionary() {
        viewModelScope.launch {
            try {
                val offline = db.offlineDictionaryDao().getAllWords()
                val sync = db.syncDictionaryDao().getAllWords()
                val merged = mergeDictionary(offline, sync)

                _state.value = DictionaryUiState.Success(
                    dictionaryList = merged,
                    onEvent = ::onEvent
                )
            }
            catch (e: Exception) {
                _state.value = DictionaryUiState.Error(
                    message = "Ошибка загрузки данных: ${e.message}"
                )
            }
        }
    }

    private fun mergeDictionary(
        offlineList: List<DictionaryItem>,
        syncList: List<DictionaryItem>
    ): List<DictionaryItem> {
        val offlineMap = offlineList.associateBy { it.word }
        val syncMap = syncList.associateBy { it.word }
        return (syncMap + offlineMap).values.toList()
    }

    fun getFilteredList(state: DictionaryUiState.Success): List<DictionaryItem> {
        val query = state.searchQuery
        return state.dictionaryList.filter {
            it.word.contains(query, ignoreCase = true)
        }
    }
}