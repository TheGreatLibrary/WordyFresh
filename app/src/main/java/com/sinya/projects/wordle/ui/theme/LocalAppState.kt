package com.sinya.projects.wordle.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import com.sinya.projects.wordle.data.local.datastore.SettingsEngine

val LocalSettingsEngine = staticCompositionLocalOf<SettingsEngine> {
    error("SettingsEngine not provided")
}

val LocalWordyColors = staticCompositionLocalOf { LightWordyColors }
