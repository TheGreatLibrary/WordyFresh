package com.sinya.projects.wordle.data.local.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sinya.projects.wordle.domain.model.data.AppSettings
import com.sinya.projects.wordle.screen.game.model.Game
import com.sinya.projects.wordle.utils.getInitialAppLocale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json


object AppDataStore {
    private val Context.dataStore by preferencesDataStore(name = "settings")

    private val RATING_WORDS_KEY = booleanPreferencesKey("rating_words")
    private val CONFETTI_KEY = booleanPreferencesKey("confetti")
    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")

    private val KEYBOARD_MODE_KEY = intPreferencesKey("keyboard_mode")

    private val LANGUAGE_KEY = stringPreferencesKey("language")
    private val LAST_GAME_STATE_KEY = stringPreferencesKey("last_game_state")

    private val ONBOARDING_KEY = booleanPreferencesKey("onboarding")

    // 🌙 Тема
    suspend fun setDarkMode(context: Context, isDark: Boolean) {
        context.dataStore.edit { it[DARK_MODE_KEY] = isDark }
    }

    fun getDarkMode(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[DARK_MODE_KEY] ?: false }


    // Взрослые слова
    suspend fun setRatingWordMode(context: Context, isRating: Boolean) {
        context.dataStore.edit { it[RATING_WORDS_KEY] = isRating }
    }

    fun getRatingWordMode(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[RATING_WORDS_KEY] ?: false }


    // Конфетти
    suspend fun setConfettiMode(context: Context, isConfetti: Boolean) {
        context.dataStore.edit { it[CONFETTI_KEY] = isConfetti }
    }

    fun getConfettiMode(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[CONFETTI_KEY] ?: false }


    // 🌐 Язык
    suspend fun setLanguage(context: Context, lang: String) {
        context.dataStore.edit { it[LANGUAGE_KEY] = lang }
    }

    fun getLanguage(context: Context): Flow<String> =
        context.dataStore.data.map { it[LANGUAGE_KEY] ?: "ru" }


    // 🎮 Состояние игры
    suspend fun saveGame(context: Context, game: Game) {
        val json = Json.encodeToString(game)
        context.dataStore.edit {
            it[LAST_GAME_STATE_KEY] = json
        }
    }

    suspend fun clearSavedGame(context: Context) {
        context.dataStore.edit {
            it.remove(LAST_GAME_STATE_KEY)
        }
    }

    suspend fun loadGame(context: Context): Game? {
        val json = context.dataStore.data
            .map { it[LAST_GAME_STATE_KEY] }
            .firstOrNull()

        return json?.let {
            try {
                Json.decodeFromString<Game>(it)
            } catch (e: Exception) {
                Log.d("Пизда", "ошибка $e")
                null
            }
        }
    }

    fun getSavedGame(context: Context): Flow<Game?> =
        context.dataStore.data.map { prefs ->
            prefs[LAST_GAME_STATE_KEY]?.let {
                try {
                    Json.decodeFromString<Game>(it)
                } catch (e: Exception) {
                    Log.e("AppDataStore", "Ошибка при разборе Game: $e")
                    null
                }
            }
        }


    // Положение клавиатуры
    suspend fun setKeyboardMode(context: Context, mode: Int) {
        context.dataStore.edit { it[KEYBOARD_MODE_KEY] = mode }
    }

    fun getKeyboardMode(context: Context): Flow<Int> =
        context.dataStore.data.map { it[KEYBOARD_MODE_KEY] ?: 0 }


    // ✅ Если хочешь получить всё сразу
    suspend fun getSettings(context: Context): AppSettings {
        val prefs = context.dataStore.data.first()
        return AppSettings(
            languageCode = prefs[LANGUAGE_KEY] ?: getInitialAppLocale(context),
            isDarkTheme = prefs[DARK_MODE_KEY] ?: false
        )
    }


    // показ онбординга
    suspend fun setOnboardingMode(context: Context, state: Boolean) {
        context.dataStore.edit { it[ONBOARDING_KEY] = state }
    }

    fun getOnboardingMode(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[ONBOARDING_KEY] ?: false }
}