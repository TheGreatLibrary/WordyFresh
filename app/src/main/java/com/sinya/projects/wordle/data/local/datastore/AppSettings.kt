package com.sinya.projects.wordle.data.local.datastore

import com.sinya.projects.wordle.screen.settings.BackgroundSetting

data class AppSettings(
    val languageCode: String,
    val backgroundItem: BackgroundSetting,
    val isFirstPlay: Boolean,
    val isDark: Boolean
)