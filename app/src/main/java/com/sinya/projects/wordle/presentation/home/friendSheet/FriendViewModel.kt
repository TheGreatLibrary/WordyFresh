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

    private val _state = MutableStateFlow(FriendUiState())
    val state: StateFlow<FriendUiState> = _state.asStateFlow()

    private val _copyRequest = MutableSharedFlow<String>()
    val copyRequest = _copyRequest.asSharedFlow()

    fun onEvent(event: FriendEvent) {
        when (event) {
            is FriendEvent.OnHiddenPlaceChange -> {
                _state.update {
                    it.copy(
                        hiddenPlace = event.newValue,
                        isError = false
                    )
                }
            }

            is FriendEvent.OnGuessedPlaceChange -> {
                _state.update {
                    it.copy(
                        guessedPlace = event.newValue,
                        isError = false
                    )
                }
            }

            is FriendEvent.OnTabClick -> {
                _state.update {
                    it.copy(
                        selectedTab = event.selectedTab,
                        guessedPlace = "",
                        hiddenPlace = "",
                        isError = false
                    )
                }
            }

            is FriendEvent.DecodeCipher -> navigateToGame(event.navigateTo)


            FriendEvent.EncodeCipher -> requestCopyCipher()
        }
    }

    private fun encode(input: String): String {
        return Base64.encodeToString(input.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
    }

    private fun decode(input: String): String {
        return String(Base64.decode(input, Base64.NO_WRAP), Charsets.UTF_8)
    }

    private fun requestCopyCipher() {
        val word = _state.value.hiddenPlace.trim().uppercase()

        viewModelScope.launch {
            getWordUseCase(word).fold(
                onSuccess = {
                    val cipher = encode(word)
                    _copyRequest.emit(cipher)
                    _state.update { it.copy(isError = false) }
                },
                onFailure = {
                    Log.e("FriendViewModel", "Encode error", it)
                    _state.update { it.copy(isError = true) }
                }
            )
        }
    }

    private fun navigateToGame(navigateTo: (ScreenRoute) -> Unit) {
        val word = _state.value.guessedPlace.trim().let { decode(it).uppercase() }

        viewModelScope.launch {
            getWordUseCase(word).fold(
                onSuccess = { wordData ->
                    navigateTo(
                        ScreenRoute.Game(
                            mode = GameMode.FRIENDLY.id,
                            wordLength = word.length,
                            lang = wordData.language,
                            word = word
                        )
                    )
                    _state.update { it.copy(isError = false) }
                },
                onFailure = {
                    Log.e("FriendViewModel", "Decode error", it)
                    _state.update { it.copy(isError = true) }
                }
            )
        }
    }
}