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
import com.sinya.projects.wordle.utils.getDefinitionWithFallback
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DictionaryViewModel(
    private val db: AppDatabase,
) : ViewModel() {

    private val _uiState = mutableStateOf(DictionaryUi())
    val uiState: State<DictionaryUi> = _uiState

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
        when(event) {
            is DictionaryUiEvent.OnRefreshList -> {
                viewModelScope.launch {
                    _uiState.value = _uiState.value.copy(isRefreshing = true)
                    // TODO: здесь добавить обновление с Supabase
                    delay(1000)
                    _uiState.value = _uiState.value.copy(isRefreshing = false)
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
                _uiState.value = _uiState.value.copy(searchQuery = event.query)
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
            val offline = db.offlineDictionaryDao().getAllWords()
            val sync = db.syncDictionaryDao().getAllWords()
            val merged = mergeDictionary(offline, sync)

            _uiState.value = _uiState.value.copy(dictionaryList = merged)
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

    fun getFilteredList(): List<DictionaryItem> {
        val query = _uiState.value.searchQuery
        return _uiState.value.dictionaryList.filter {
            it.word.contains(query, ignoreCase = true)
        }
    }
}