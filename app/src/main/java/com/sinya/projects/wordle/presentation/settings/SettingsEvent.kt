package com.sinya.projects.wordle.presentation.settings

import com.sinya.projects.wordle.domain.enums.BackgroundSettings

sealed interface SettingsEvent {
    data class SetBackground(val background: BackgroundSettings) : SettingsEvent
    data class SetLanguage(val lang: String) : SettingsEvent
    data class SetKeyboard(val code: Int) : SettingsEvent

    data class KeyboardSheetState(val show: Boolean) : SettingsEvent
    data class LanguageSheetState(val show: Boolean) : SettingsEvent

    data class ToggleTheme(val isDark: Boolean) : SettingsEvent
    data class ToggleConfetti(val enabled: Boolean) : SettingsEvent
    data class ToggleShowLetterHints(val enabled: Boolean) : SettingsEvent
    data class ToggleRating(val enabled: Boolean) : SettingsEvent
    data class ToggleShowSavedGameDialog(val enabled: Boolean): SettingsEvent
    data class ToggleVibration(val enabled: Boolean) : SettingsEvent

    data object ClearBackground : SettingsEvent
    data object SendSupport : SettingsEvent
}