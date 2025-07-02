package com.sinya.projects.wordle.screen.home.friend_dialog

import android.util.Base64
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.data.local.dao.WordDao
import com.sinya.projects.wordle.navigation.ScreenRoute
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class FriendViewModel(private val wordDao: WordDao) : ViewModel() {

    private val _state = mutableStateOf(FriendModeUiState())
    val state: State<FriendModeUiState> = _state

    private val _copyRequest = MutableSharedFlow<String>()
    val copyRequest = _copyRequest.asSharedFlow()

    companion object {
        fun provideFactory(
            wordDao: WordDao
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return FriendViewModel(wordDao) as T
                }
            }
        }
    }

    fun onEvent(event: FriendModeUiEvent) {
        when(event) {
            is FriendModeUiEvent.OnHiddenPlaceChange -> {
                _state.value = _state.value.copy(
                    hiddenPlace = event.newValue,
                    isError = false
                )
            }
            is FriendModeUiEvent.OnGuessedPlaceChange -> {
                _state.value = _state.value.copy(
                    guessedPlace = event.newValue,
                    isError = false
                )
            }
            is FriendModeUiEvent.OnTabClick -> {
                _state.value = _state.value.copy(
                    selectedTab = event.selectedTab,
                    guessedPlace = "",
                    hiddenPlace = "",
                    isError = false
                )
            }
            is FriendModeUiEvent.EncodeCipher -> requestCopyCipher()
            is FriendModeUiEvent.DecodeCipher -> navigateToGame(event.navigateTo)
        }
    }

    private fun encode(input: String): String {
        return Base64.encodeToString(input.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
    }

    private fun decode(input: String): String {
        return String(Base64.decode(input, Base64.NO_WRAP), Charsets.UTF_8)
    }

    private fun requestCopyCipher() {
        _state.value = _state.value.copy(hiddenPlace = _state.value.hiddenPlace.trim())
        val word = _state.value.hiddenPlace.uppercase()

        viewModelScope.launch {
            val exists = wordDao.exists(word)
            if (exists) {
                val cipher = encode(word)
                _copyRequest.emit(cipher)
                _state.value = _state.value.copy(isError = false)
            } else {
                _state.value = _state.value.copy(isError = true)
            }
        }
    }

    private fun navigateToGame(navigateTo: (ScreenRoute) -> Unit) {
        _state.value = _state.value.copy(guessedPlace = _state.value.guessedPlace.trim())
        val word = decode(_state.value.guessedPlace).uppercase()

        viewModelScope.launch {
            val lang = wordDao.getWordLang(word)

            if (lang != null) {
                navigateTo(ScreenRoute.Game(
                    mode = 2,
                    wordLength = word.length,
                    lang = lang,
                    word = word
                ))
                _state.value = _state.value.copy(isError = false)
            } else {
                _state.value = _state.value.copy(isError = true)
            }
        }
    }
}