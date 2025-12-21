package com.sinya.projects.wordle.data.local.datastore

import com.sinya.projects.wordle.domain.enums.BackgroundSettings

data class AppSettings(
    val languageCode: String,
    val backgroundItem: BackgroundSettings,
    val isFirstPlay: Boolean?,
    val isDark: Boolean
)