package com.sinya.projects.wordle.presentation.settings

import com.sinya.projects.wordle.domain.enums.TypeKeyboards
import com.sinya.projects.wordle.domain.enums.TypeLanguages
import com.sinya.projects.wordle.domain.enums.TypeThemes
import com.sinya.projects.wordle.domain.enums.BackgroundSettings

sealed interface SettingsUiState {
    data object Loading : SettingsUiState

    data class Success(
        val backgroundSetting: BackgroundSettings = BackgroundSettings.DEFAULT,
        val currentTheme: TypeThemes = TypeThemes.LIGHT,
        val currentLang: TypeLanguages = TypeLanguages.RU,
        val currentKeyboard: TypeKeyboards = TypeKeyboards.WORDLE,
        val confettiEnabled: Boolean = true,
        val ratingModeEnabled: Boolean = false,
        val showLanguageSheet: Boolean = false,
        val showKeyboardSheet: Boolean = false
    ) : SettingsUiState
}
