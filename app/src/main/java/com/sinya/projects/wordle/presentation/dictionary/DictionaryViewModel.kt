package com.sinya.projects.wordle.presentation.dictionary

import android.util.Log
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

            is DictionaryEvent.OnSearchQueryChanged -> updateIfSuccess {
                it.copy(searchQuery = event.query)
            }

            DictionaryEvent.OnClearAll -> clearAllDictionary()

            DictionaryEvent.OnErrorShown -> updateIfSuccess { it.copy(errorMessage = null) }
        }
    }

    private fun reloadDefinition(item: DictionaryItem) {
        updateIfSuccess {
            it.copy(dictionaryList = it.dictionaryList.map { items ->
                if (items.id == item.id) items.copy(isLoading = true)
                else items
            })
        }

        viewModelScope.launch {
            updateWordDescriptionUseCase(item.word).fold(
                onSuccess = {
                    updateIfSuccess {
                        it.copy(dictionaryList = it.dictionaryList.map { items ->
                            if (items.id == item.id) items.copy(isLoading = false)
                            else items
                        })
                    }
                },
                onFailure = { exception ->
                    updateIfSuccess {
                        it.copy(
                            errorMessage = getErrorMessage(exception),
                            dictionaryList = it.dictionaryList.map { items ->
                                if (items.id == item.id) items.copy(isLoading = false)
                                else items
                            })
                    }
                }
            )
        }
    }

    private fun clearAllDictionary() = viewModelScope.launch {
        clearAllDictionaryUseCase().fold(
            onSuccess = { },
            onFailure = { exception ->
                updateIfSuccess {
                    it.copy(errorMessage = "Ошибка очистки: ${exception.message}")
                }
            }
        )
    }

    private fun refreshDictionary() {
        updateIfSuccess { it.copy(isRefreshing = true) }

        viewModelScope.launch {
            syncDictionaryUseCase().fold(
                onSuccess = {
                    Log.d("Dictionary", "Uspech")
                    updateIfSuccess {
                        it.copy(isRefreshing = false)
                    }
                },
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
    }

    private fun loadDictionary() = viewModelScope.launch {
        getAllWordsUseCase().collect { words ->
            if (_state.value is DictionaryUiState.Success) {
                updateIfSuccess {
                    it.copy(dictionaryList = words, isRefreshing = false)
                }
            } else {
                _state.update {
                    DictionaryUiState.Success(dictionaryList = words)
                }
            }
        }
    }

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