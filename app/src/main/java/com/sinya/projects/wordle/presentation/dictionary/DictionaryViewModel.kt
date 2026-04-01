package com.sinya.projects.wordle.presentation.dictionary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.domain.model.DictionaryItem
import com.sinya.projects.wordle.domain.useCase.ClearAllDictionaryUseCase
import com.sinya.projects.wordle.domain.useCase.GetAllWordsUseCase
import com.sinya.projects.wordle.domain.useCase.InsertOrUpdateDefinitionUseCase
import com.sinya.projects.wordle.domain.useCase.SyncDictionaryUseCase
import com.sinya.projects.wordle.utils.VibrationManager
import com.sinya.projects.wordle.utils.getErrorMessage
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
    private val syncDictionaryUseCase: SyncDictionaryUseCase,
    private val vibrationManager: VibrationManager
) : ViewModel() {

    private val _state = MutableStateFlow<DictionaryUiState>(DictionaryUiState.Loading)
    val state: StateFlow<DictionaryUiState> = _state.asStateFlow()

    @OptIn(FlowPreview::class)
    val filteredList: StateFlow<List<DictionaryItem>> = state
        .debounce(200)
        .map { currentState ->
            when (currentState) {
                is DictionaryUiState.Success -> {
                    val query = currentState.searchQuery
                    val list = if (query.isEmpty()) {
                        currentState.dictionaryList
                    } else {
                        currentState.dictionaryList.filter {
                            it.word.contains(query, ignoreCase = true)
                        }
                    }

                    currentState.modes.fold(list) { exercises, mode ->
                        mode.filter(exercises)
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

            is DictionaryEvent.OnVibrate -> vibrationManager.vibrate(event.type)
            is DictionaryEvent.SortParamChange -> updateIfSuccess {
                val updatedList = it.modes.map { mode ->
                    if (mode == event.mode) {
                        mode.apply(event.onSelect)
                    } else {
                        mode
                    }
                }
                it.copy(
                    modes = updatedList

                )
            }
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
                            errorMessage = exception.getErrorMessage(),
                            dictionaryList = it.dictionaryList.map { items ->
                                if (items.id == item.id) items.copy(isLoading = false)
                                else items
                            }
                        )
                    }
                }
            )
        }
    }

    private fun clearAllDictionary() = viewModelScope.launch {
        clearAllDictionaryUseCase().onFailure { exception ->
            updateIfSuccess {
                it.copy(errorMessage = exception.getErrorMessage())
            }
        }
    }

    private fun refreshDictionary() {
        updateIfSuccess { it.copy(isRefreshing = true) }

        viewModelScope.launch {
            syncDictionaryUseCase().fold(
                onSuccess = {
                    updateIfSuccess {
                        it.copy(isRefreshing = false)
                    }
                },
                onFailure = { exception ->
                    updateIfSuccess {
                        it.copy(
                            isRefreshing = false,
                            errorMessage = exception.getErrorMessage()
                        )
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
}