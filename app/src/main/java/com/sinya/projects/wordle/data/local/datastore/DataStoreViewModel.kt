package com.sinya.projects.wordle.data.local.datastore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.domain.model.Game
import com.sinya.projects.wordle.domain.enums.BackgroundSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface SavedGameState {
    data object Loading : SavedGameState
    data class Loaded(val game: Game?) : SavedGameState
}

@HiltViewModel
class DataStoreViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    val darkMode = dataStoreManager.getDarkMode()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val ratingWordMode = dataStoreManager.getRatingWordMode()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val confettiMode = dataStoreManager.getConfettiMode()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val language = dataStoreManager.getLanguage()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "ru")

    val onboardingCompleted = dataStoreManager.getOnboardingMode()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000), null)

    val keyboardMode = dataStoreManager.getKeyboardMode()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val background = dataStoreManager.getBackground()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val savedGame = dataStoreManager.getSavedGame()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val savedGameState: StateFlow<SavedGameState> = dataStoreManager.getSavedGame()
        .map<Game?, SavedGameState> { game ->
            SavedGameState.Loaded(game)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SavedGameState.Loading
        )

    val appSettings = combine(
        language,
        darkMode,
        onboardingCompleted,
        background
    ) { lang, dark, onboarding, bg ->
        AppSettings(
            languageCode = lang,
            isDark = dark,
            isFirstPlay = onboarding,
            backgroundItem = BackgroundSettings.fromName(bg?: BackgroundSettings.DEFAULT.name)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings(
        languageCode = "ru",
        isDark = false,
        isFirstPlay = false,
        backgroundItem = BackgroundSettings.DEFAULT
    ))

    fun setDarkMode(value: Boolean) {
        viewModelScope.launch {
            dataStoreManager.setDarkMode(value)
        }
    }

    fun setRatingWordMode(value: Boolean) {
        viewModelScope.launch {
            dataStoreManager.setRatingWordMode(value)
        }
    }

    fun setConfettiMode(value: Boolean) {
        viewModelScope.launch {
            dataStoreManager.setConfettiMode(value)
        }
    }

    fun setLanguage(value: String) {
        viewModelScope.launch {
            dataStoreManager.setLanguage(value)
        }
    }

    fun setOnboardingCompleted(value: Boolean) {
        viewModelScope.launch {
            dataStoreManager.setOnboardingMode(value)
        }
    }

    fun setKeyboardMode(value: Int) {
        viewModelScope.launch {
            dataStoreManager.setKeyboardMode(value)
        }
    }

    fun setBackground(value: BackgroundSettings) {
        viewModelScope.launch {
            dataStoreManager.setBackground(value)
        }
    }

    fun clearBackground() {
        viewModelScope.launch {
            dataStoreManager.clearBackground()
        }
    }

    // Game operations
    fun saveGame(game: Game) {
        viewModelScope.launch {
            dataStoreManager.saveGame(game)
        }
    }

    fun clearSavedGame() {
        viewModelScope.launch {
            dataStoreManager.clearSavedGame()
        }
    }

    suspend fun loadGame(): Game? {
        return dataStoreManager.loadGame()
    }
}