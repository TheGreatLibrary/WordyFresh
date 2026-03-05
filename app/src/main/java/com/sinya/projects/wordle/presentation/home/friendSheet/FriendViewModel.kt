package com.sinya.projects.wordle.presentation.home.friendSheet

import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.domain.enums.GameMode
import com.sinya.projects.wordle.domain.useCase.GetDataWordUseCase
import com.sinya.projects.wordle.navigation.ScreenRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val getWordUseCase: GetDataWordUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<FriendUiState>(FriendUiState.FriendForm())
    val state: StateFlow<FriendUiState> = _state.asStateFlow()

    private val _copyRequest = MutableSharedFlow<String>()
    val copyRequest = _copyRequest.asSharedFlow()

    fun onEvent(event: FriendEvent) {
        when (event) {
            is FriendEvent.OnHiddenPlaceChange -> updateIFriendForm {
                it.copy(
                    hiddenPlace = event.newValue,
                    isError = false
                )
            }

            is FriendEvent.OnGuessedPlaceChange -> updateIFriendForm {
                it.copy(
                    guessedPlace = event.newValue,
                    isError = false
                )
            }

            is FriendEvent.OnTabClick -> updateIFriendForm {
                it.copy(
                    selectedTab = event.selectedTab,
                    guessedPlace = "",
                    hiddenPlace = "",
                    isError = false
                )
            }

            is FriendEvent.DecodeCipher -> navigateToGame()

            FriendEvent.EncodeCipher -> requestCopyCipher()

            FriendEvent.ClearState -> clearState()
        }
    }

    private fun encode(input: String): String =
        Base64.encodeToString(input.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)

    private fun decode(input: String): String? {
        return try {
            String(Base64.decode(input, Base64.NO_WRAP), Charsets.UTF_8)
        } catch (e: IllegalArgumentException) {
            null
        }
    }
    private fun clearState() {
        _state.value = FriendUiState.FriendForm(
            selectedTab = 0,
            hiddenPlace = "",
            guessedPlace = "",
            isError = false
        )
    }

    private fun requestCopyCipher() {
        val s = _state.value as? FriendUiState.FriendForm ?: return
        val word = s.hiddenPlace.trim().uppercase()
        updateIFriendForm { it.copy(isLoading = true) }

        viewModelScope.launch {
            getWordUseCase(word).fold(
                onSuccess = {
                    val cipher = encode(word)
                    _copyRequest.emit(cipher)
                    updateIFriendForm {
                        it.copy(isError = false, isLoading = false)
                    }
                },
                onFailure = {
                    Log.e("FriendViewModel", "Encode error", it)
                    updateIFriendForm { form ->
                        form.copy(isError = true, isLoading = false)
                    }
                }
            )
        }
    }

    private fun navigateToGame() {
        val s = _state.value as? FriendUiState.FriendForm ?: return
        val encoded = s.guessedPlace.trim()
        updateIFriendForm { it.copy(isLoading = true) }

        val word = decode(encoded)?.uppercase()
        if (word == null) {
            updateIFriendForm { form ->
                form.copy(isError = true, isLoading = false)
            }
            return
        }

        viewModelScope.launch {
            getWordUseCase(word).fold(
                onSuccess = { wordData ->
                    _state.value = FriendUiState.Success(
                        ScreenRoute.Game(
                            mode = GameMode.FRIENDLY.id,
                            wordLength = wordData.length,
                            lang = wordData.language,
                            word = wordData.word
                        )
                    )
                },
                onFailure = {
                    Log.e("FriendViewModel", "Decode error", it)
                    updateIFriendForm { form ->
                        form.copy(isError = true, isLoading = false)
                    }
                }
            )
        }
    }

    private fun updateIFriendForm(transform: (FriendUiState.FriendForm) -> FriendUiState.FriendForm) {
        _state.update { currentState ->
            if (currentState is FriendUiState.FriendForm) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }
}