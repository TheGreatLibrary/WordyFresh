package com.sinya.projects.wordle.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sinya.projects.wordle.domain.enums.BackgroundSettings
import com.sinya.projects.wordle.domain.enums.TypeKeyboards
import com.sinya.projects.wordle.domain.enums.TypeLanguages
import com.sinya.projects.wordle.domain.model.CriticalPrefs
import com.sinya.projects.wordle.domain.model.Game
import com.sinya.projects.wordle.domain.model.OptionalPrefs
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json


sealed interface SavedGameState {
    data object Loading : SavedGameState
    data class Loaded(val game: Game?) : SavedGameState
}

@Singleton
class DataStoreManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private val RATING_WORDS_KEY = booleanPreferencesKey("rating_words")
    private val CONFETTI_KEY = booleanPreferencesKey("confetti")
    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
    private val SHOW_LETTER_HINTS = booleanPreferencesKey("show_letter_hints")
    private val SHOW_SAVED_GAME_DIALOG = booleanPreferencesKey("show_saved_game_dialog")
    private val VIBRATION_STATUS = booleanPreferencesKey("vibration_status")


    private val KEYBOARD_MODE_KEY = intPreferencesKey("keyboard_mode")

    private val LANGUAGE_KEY = stringPreferencesKey("language")
    private val LAST_GAME_STATE_KEY = stringPreferencesKey("last_game_state")
    private val BACKGROUND_SETTING_KEY = stringPreferencesKey("background_setting")

    private val ONBOARDING_KEY = booleanPreferencesKey("onboarding")


    private suspend fun <T> save(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { settings ->
            settings[key] = value
        }
    }

    private fun <T> read(key: Preferences.Key<T>, defaultValue: T): Flow<T> = context.dataStore.data
        .map { settings ->
            settings[key] ?: defaultValue
        }

    private suspend fun <T> remove(key: Preferences.Key<T>) {
        context.dataStore.edit { settings ->
            settings.remove(key)
        }
    }

    suspend fun readAllCriticalPreferences(): CriticalPrefs {
        val prefs = context.dataStore.data.first()
        return CriticalPrefs(
            dark = prefs[DARK_MODE_KEY] ?: false,
            language = prefs[LANGUAGE_KEY] ?: TypeLanguages.RU.code,
            onboardingDone = prefs[ONBOARDING_KEY] ?: false,
        )
    }

    suspend fun readAllOptionalPreferences(): OptionalPrefs {
        val prefs = context.dataStore.data.first()
        return OptionalPrefs(
            background = prefs[BACKGROUND_SETTING_KEY] ?: BackgroundSettings.DEFAULT.name,
            vibrationStatus = prefs[VIBRATION_STATUS] ?: true,
            ratingWords = prefs[RATING_WORDS_KEY] ?: false,
            confetti = prefs[CONFETTI_KEY] ?: true,
            showLetterHint = prefs[SHOW_LETTER_HINTS] ?: true,
            showSavedGameDialogState = prefs[SHOW_SAVED_GAME_DIALOG] ?: true,
            keyboardMode = prefs[KEYBOARD_MODE_KEY] ?: TypeKeyboards.WORDLE.code,
            lastGame = SavedGameState.Loaded(prefs[LAST_GAME_STATE_KEY]?.let {
                try {
                    Json.decodeFromString<Game>(it)
                } catch (_: Exception) {
                    null
                }
            })
        )
    }

    suspend fun setBackground(value: BackgroundSettings) = save(BACKGROUND_SETTING_KEY, value.name)
    suspend fun clearBackground() = remove(BACKGROUND_SETTING_KEY)
    suspend fun setDarkMode(value: Boolean) = save(DARK_MODE_KEY, value)
    suspend fun setRatingWordMode(value: Boolean) = save(RATING_WORDS_KEY, value)
    suspend fun setConfettiMode(value: Boolean) = save(CONFETTI_KEY, value)
    suspend fun setLanguage(value: String) = save(LANGUAGE_KEY, value)
    suspend fun setOnboardingMode(value: Boolean) = save(ONBOARDING_KEY, value)
    suspend fun setKeyboardMode(value: Int) = save(KEYBOARD_MODE_KEY, value)
    suspend fun saveGame(value: Game) = save(LAST_GAME_STATE_KEY, Json.encodeToString(value))
    suspend fun clearSavedGame() = remove(LAST_GAME_STATE_KEY)
    suspend fun setShowLetterHints(value: Boolean) = save(SHOW_LETTER_HINTS, value)
    suspend fun setShowSavedGameDialogState(value: Boolean) = save(SHOW_SAVED_GAME_DIALOG, value)
    suspend fun setVibrationState(value: Boolean) = save(VIBRATION_STATUS, value)
}

