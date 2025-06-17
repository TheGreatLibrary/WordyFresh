package com.sinya.projects.wordle.utils

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

fun Context.withLocale(locale: Locale): Context {
    val config = resources.configuration
    val newConfig = Configuration(config)
    newConfig.setLocale(locale)
    return createConfigurationContext(newConfig)
}