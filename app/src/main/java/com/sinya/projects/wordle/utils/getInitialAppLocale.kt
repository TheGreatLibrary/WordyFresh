package com.sinya.projects.wordle.utils

import android.content.Context
import com.sinya.projects.wordle.screen.language.AppLanguages

fun Context.getInitialAppLocale(): String {
    val systemLang = resources.configuration.locales[0].language
    return AppLanguages.getCode(systemLang) ?: "ru"
}