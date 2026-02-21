package com.sinya.projects.wordle.presentation.dictionary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.domain.error.DefinitionNotFoundException
import com.sinya.projects.wordle.domain.error.NoInternetException
import com.sinya.projects.wordle.domain.error.UserNotAuthenticatedException
import com.sinya.projects.wordle.domain.model.DictionaryItem
import com.sinya.projects.wordle.domain.useCase.ClearAllDictionaryUseCase
import com.sinya.projects.wordle.domain.useCase.GetAllWordsUseCase
import com.sinya.projects.wordle.domain.useCase.InsertOrUpdateDefinitionUseCase
import com.sinya.projects.wordle.domain.useCase.SyncDictionaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class DictionaryViewModel @Inject constructor(
    private val getAllWordsUseCase: GetAllWordsUseCase,
    private val updateWordDescriptionUseCase: InsertOrUpdateDefinitionUseCase,
    private val clearAllDictionaryUseCase: ClearAllDictionaryUseCase,
    private val syncDictionaryUseCase: SyncDictionaryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<DictionaryUiState>(DictionaryUiState.Loading)
    val state: StateFlow<DictionaryUiState> = _state.asStateFlow()

    @OptIn(FlowPreview::class)
    val filteredList: StateFlow<List<DictionaryItem>> = state
        .debounce(300)
        .map { currentState ->
            when (currentState) {
                is DictionaryUiState.Success -> {
                    val query = currentState.searchQuery
                    if (query.isEmpty()) {
                        currentState.dictionaryList
                    } else {
                        currentState.dictionaryList.filter {
                            it.word.contains(query, ignoreCase = true)
                        }
                    }
                }
                else -> emptyList()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        loadDictionary()
    }

    fun onEvent(event: DictionaryEvent) {
        when (event) {
            DictionaryEvent.OnRefresh -> refreshDictionary()
            is DictionaryEvent.OnReloadedDefinition -> reloadDefinition(event.item)
            is DictionaryEvent.OnSearchQueryChanged -> updateSearchQuery(event.query)
            DictionaryEvent.OnClearAll -> clearAllDictionary()
            DictionaryEvent.OnErrorShown -> onErrorShown()
        }
    }

    private fun onErrorShown() {
        _state.update { currentState ->
            if (currentState is DictionaryUiState.Success) {
                currentState.copy(errorMessage = null)
            } else {
                currentState
            }
        }
    }

    private fun updateSearchQuery(query: String) {
        _state.update { currentState ->
            if (currentState is DictionaryUiState.Success) {
                currentState.copy(searchQuery = query)
            } else {
                currentState
            }
        }
    }

    private fun reloadDefinition(item: DictionaryItem) = viewModelScope.launch {
        updateWordDescriptionUseCase(item.word).fold(
            onSuccess = { /*loadDictionary()*/ },
            onFailure = { exception ->
                updateIfSuccess {
                    it.copy(errorMessage = getErrorMessage(exception))
                }
            }
        )
    }

    private fun clearAllDictionary() = viewModelScope.launch {
        clearAllDictionaryUseCase().fold(
            onSuccess = { /*loadDictionary()*/ },
            onFailure = { exception ->
                updateIfSuccess {
                    it.copy(errorMessage = "Ошибка очистки: ${exception.message}")
                }
            }
        )
    }

    private fun refreshDictionary() = viewModelScope.launch {
        updateIfSuccess { it.copy(isRefreshing = true) }

        syncDictionaryUseCase().fold(
            onSuccess = { loadDictionary() },
            onFailure = { exception ->
                val errorMessage = when (exception) {
                    is UserNotAuthenticatedException -> "Требуется авторизация"
                    else -> "Ошибка синхронизации: ${exception.message}"
                }
                updateIfSuccess {
                    it.copy(isRefreshing = false, errorMessage = errorMessage)
                }
            }
        )
    }

    private fun loadDictionary() = viewModelScope.launch {
        getAllWordsUseCase().collect { words ->
            if (_state.value is DictionaryUiState.Success) {
                updateIfSuccess {
                    it.copy(dictionaryList = words)
                }
            }
            else {
                _state.update {
                    DictionaryUiState.Success(dictionaryList = words)
                }
            }
        }
    }

    /** доп. методы */

    private fun updateIfSuccess(transform: (DictionaryUiState.Success) -> DictionaryUiState.Success) {
        _state.update { currentState ->
            if (currentState is DictionaryUiState.Success) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }

    private fun getErrorMessage(exception: Throwable): String {
        return when (exception) {
            is NoInternetException -> "Нет подключения к интернету"
            is DefinitionNotFoundException -> "Определение не найдено"
            else -> "Ошибка: ${exception.message}"
        }
    }
}