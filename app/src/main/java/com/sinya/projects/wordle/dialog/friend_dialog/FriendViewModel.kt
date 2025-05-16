package com.sinya.projects.wordle.dialog.friend_dialog

import android.util.Base64
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.sinya.projects.wordle.data.local.dao.WordDao
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class FriendViewModel(
    private val wordDao: WordDao
) : ViewModel() {
    var hiddenPlace by mutableStateOf("")
        private set
    var guessedWord by mutableStateOf("")
        private set
    var errorText by mutableStateOf<String?>(null)
        private set

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

    fun onHiddenPlaceChange(newValue: String) {
        errorText = null
        hiddenPlace = newValue
    }

    fun onGuessedWordChange(newValue: String) {
        errorText = null
        guessedWord = newValue
    }

    fun requestCopyCipher() {
        viewModelScope.launch {
            val word = hiddenPlace.trim().uppercase()

            val exists = wordDao.exists(word)
            if (exists) {
                val cipher = encode(word)
                _copyRequest.emit(cipher)
                errorText = null
            } else {
                errorText = "Данного слова нет в базе данных!"
            }
        }
    }

    private fun encode(input: String): String {
        return Base64.encodeToString(input.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
    }

    private fun decode(input: String): String {
        return String(Base64.decode(input, Base64.NO_WRAP), Charsets.UTF_8)
    }

    fun navigateToGame(navController: NavController) {
        viewModelScope.launch {
            val decodedWord = decode(guessedWord.trim()).uppercase()
            val lang = wordDao.getWordLang(decodedWord)

            if (lang != null) {
                navController.navigate("game/2/${decodedWord.length}/$lang/${decodedWord.uppercase()}")
                errorText = null // обнуляем ошибку
            } else {
                errorText = "Данного слова нет в базе данных!"
            }
        }
    }
}