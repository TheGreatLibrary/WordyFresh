package com.sinya.projects.wordle.utils

import android.content.Context
import com.sinya.projects.wordle.screen.language.AppLanguages

fun getInitialAppLocale(context: Context): String {
    val systemLang = context.resources.configuration.locales[0].language
    return AppLanguages.getCode(systemLang) ?: "ru"
}