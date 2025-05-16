package com.sinya.projects.wordle.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sinya.projects.wordle.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


val Context.dataStore by preferencesDataStore(name = "settings")

object AppDataStore {

    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
    private val LANGUAGE_KEY = stringPreferencesKey("language")
    private val LAST_GAME_STATE_KEY = stringPreferencesKey("last_game_state")

    // 🌙 Тема
    suspend fun setDarkMode(context: Context, isDark: Boolean) {
        context.dataStore.edit { it[DARK_MODE_KEY] = isDark }
    }

    fun isDarkMode(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[DARK_MODE_KEY] ?: false }

    // 🌐 Язык
    suspend fun setLanguage(context: Context, lang: String) {
        context.dataStore.edit { it[LANGUAGE_KEY] = lang }
    }

    fun languageFlow(context: Context): Flow<String> =
        context.dataStore.data.map { it[LANGUAGE_KEY] ?: "ru" }

    // 🎮 Состояние игры
    suspend fun setGameState(context: Context, stateJson: String) {
        context.dataStore.edit { it[LAST_GAME_STATE_KEY] = stateJson }
    }

    fun getGameState(context: Context): Flow<String?> =
        context.dataStore.data.map { it[LAST_GAME_STATE_KEY] }

    // ✅ Если хочешь получить всё сразу
    suspend fun getSettings(context: Context): AppSettings {
        val prefs = context.dataStore.data.first()
        return AppSettings(
            languageCode = prefs[LANGUAGE_KEY] ?: "ru",
            isDarkTheme = prefs[DARK_MODE_KEY] ?: false
        )
    }
}