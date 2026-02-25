package com.sinya.projects.wordle.data.local.datastore

import android.content.Context
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

private val Context.dataStore by preferencesDataStore(name = "settings")

sealed interface SavedGameState {
    data object Loading : SavedGameState
    data class Loaded(val game: Game?) : SavedGameState
}

@Singleton
class DataStoreManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val RATING_WORDS_KEY = booleanPreferencesKey("rating_words")
    private val CONFETTI_KEY = booleanPreferencesKey("confetti")
    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")

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
            ratingWords = prefs[RATING_WORDS_KEY] ?: false,
            confetti = prefs[CONFETTI_KEY] ?: true,
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
//    fun getBackground(): Flow<String> = read(BACKGROUND_SETTING_KEY, BackgroundSettings.DEFAULT.name)

    suspend fun setDarkMode(value: Boolean) = save(DARK_MODE_KEY, value)
//    fun getDarkMode(): Flow<Boolean> = read(DARK_MODE_KEY, TypeThemes.LIGHT.isDark)

    suspend fun setRatingWordMode(value: Boolean) = save(RATING_WORDS_KEY, value)
//    fun getRatingWordMode(): Flow<Boolean> = read(RATING_WORDS_KEY, false)

    suspend fun setConfettiMode(value: Boolean) = save(CONFETTI_KEY, value)
//    fun getConfettiMode(): Flow<Boolean> = read(CONFETTI_KEY, true)

    suspend fun setLanguage(value: String) = save(LANGUAGE_KEY, value)
//    fun getLanguage(): Flow<String> = read(LANGUAGE_KEY, TypeLanguages.RU.code)

    suspend fun setOnboardingMode(value: Boolean) = save(ONBOARDING_KEY, value)
//    fun getOnboardingMode(): Flow<Boolean> = read(ONBOARDING_KEY, false)

    suspend fun setKeyboardMode(value: Int) = save(KEYBOARD_MODE_KEY, value)
//    fun getKeyboardMode(): Flow<Int> = read(KEYBOARD_MODE_KEY, TypeKeyboards.WORDLE.code)

    suspend fun saveGame(value: Game) = save(LAST_GAME_STATE_KEY, Json.encodeToString(value))
    suspend fun clearSavedGame() = remove(LAST_GAME_STATE_KEY)
//    suspend fun loadGame(): Game? {
//        val json = context.dataStore.data
//            .map { it[LAST_GAME_STATE_KEY] }
//            .firstOrNull()
//
//        return json?.let {
//            try {
//                Json.decodeFromString<Game>(it)
//            } catch (e: Exception) {
//                null
//            }
//        }
//    }
//
//    fun getSavedGame(): Flow<Game?> =
//        context.dataStore.data.map { prefs ->
//            prefs[LAST_GAME_STATE_KEY]?.let {
//                try {
//                    Json.decodeFromString<Game>(it)
//                } catch (e: Exception) {
//                    Log.e("AppDataStore", "Ошибка при разборе Game: $e")
//                    null
//                }
//            }
//        }
}

