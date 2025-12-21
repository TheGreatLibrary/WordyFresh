package com.sinya.projects.wordle.presentation.settings

import com.sinya.projects.wordle.domain.enums.TypeKeyboards
import com.sinya.projects.wordle.domain.enums.TypeLanguages
import com.sinya.projects.wordle.domain.enums.TypeThemes
import com.sinya.projects.wordle.domain.enums.BackgroundSettings

data class SettingsUiState(
    val backgroundSetting: BackgroundSettings = BackgroundSettings.DEFAULT,
    val currentTheme: TypeThemes = TypeThemes.LIGHT,
    val currentLang: TypeLanguages = TypeLanguages.RU,
    val currentKeyboard: TypeKeyboards = TypeKeyboards.WORDLE,
    val confettiEnabled: Boolean = true,
    val ratingModeEnabled: Boolean = false,
    val showLanguageSheet: Boolean = false,
    val showKeyboardSheet: Boolean = false
)