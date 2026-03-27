package com.sinya.projects.wordle.data.local.datastore

import com.sinya.projects.wordle.domain.enums.BackgroundSettings
import com.sinya.projects.wordle.domain.enums.TypeKeyboards
import com.sinya.projects.wordle.domain.enums.TypeLanguages
import com.sinya.projects.wordle.domain.model.Game
import com.sinya.projects.wordle.domain.model.UiConfig
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Singleton
class SettingsEngine @Inject constructor(
    private val store: DataStoreManager
) {
    private val _state = AtomicState(
        UiConfig(
            dark = false,
            language = TypeLanguages.RU.code,
            onboardingDone = null,
            background = BackgroundSettings.DEFAULT.name,
            ratingWords = false,
            confetti = true,
            showLetterHints = true,
            vibrationStatus = true,
            showSavedGameDialogState = true,
            keyboardMode = TypeKeyboards.WORDLE.code,
            lastGame = SavedGameState.Loading
        )
    )
    val uiState: StateFlow<UiConfig> = _state.state

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    suspend fun hydrateCritical() {
        val prefs = store.readAllCriticalPreferences()
        _state.update { current ->
            current.copy(
                dark = prefs.dark,
                language = prefs.language,
                onboardingDone = prefs.onboardingDone,
            )
        }
        hydrateOptional()
    }

    private fun hydrateOptional() {
        scope.launch {
            val prefs = store.readAllOptionalPreferences()
            _state.update { current ->
                current.copy(
                    background = prefs.background,
                    vibrationStatus = prefs.vibrationStatus,
                    ratingWords = prefs.ratingWords,
                    confetti = prefs.confetti,
                    showSavedGameDialogState = prefs.showSavedGameDialogState,
                    showLetterHints = prefs.showLetterHint,
                    keyboardMode = prefs.keyboardMode,
                    lastGame = prefs.lastGame
                )
            }
        }
    }

    fun setDark(v: Boolean) {
        _state.update { it.copy(dark = v) }
        persist { store.setDarkMode(v) }
    }

    fun setLang(v: String) {
        _state.update { it.copy(language = v) }
        persist { store.setLanguage(v) }
    }

    fun setBackground(v: BackgroundSettings) {
        _state.update { it.copy(background = v.name) }
        persist { store.setBackground(v) }
    }

    fun clearBackground() {
        _state.update { it.copy(background = BackgroundSettings.DEFAULT.name) }
        persist { store.clearBackground() }
    }

    fun setRatingWords(v: Boolean) {
        _state.update { it.copy(ratingWords = v) }
        persist { store.setRatingWordMode(v) }
    }

    fun setConfetti(v: Boolean) {
        _state.update { it.copy(confetti = v) }
        persist { store.setConfettiMode(v) }
    }

    fun setOnboardingState(v: Boolean) {
        _state.update { it.copy(onboardingDone = v) }
        persist { store.setOnboardingMode(v) }
    }

    fun setShowLetterHints(v: Boolean) {
        _state.update { it.copy(showLetterHints = v) }
        persist { store.setShowLetterHints(v) }
    }

    fun setKeyboardMode(v: Int) {
        _state.update { it.copy(keyboardMode = v) }
        persist { store.setKeyboardMode(v) }
    }

    fun setSavedGameDialogState(v: Boolean) {
        _state.update { it.copy(showSavedGameDialogState = v) }
        persist { store.setShowSavedGameDialogState(v) }
    }

    fun clearSavedGame() {
        _state.update { it.copy(lastGame = SavedGameState.Loaded(null)) }
        persist { store.clearSavedGame() }
    }

    fun saveGame(game: Game) {
        _state.update { it.copy(lastGame = SavedGameState.Loaded(game)) }
        persist { store.saveGame(game) }
    }

    fun setVibrationState(v: Boolean) {
        _state.update { it.copy(vibrationStatus = v) }
        persist { store.setVibrationState(v) }
    }

    private fun persist(block: suspend () -> Unit) {
        scope.launch { runCatching { block() } }
    }
}