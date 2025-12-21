package com.sinya.projects.wordle.utils

import android.content.Context
import com.sinya.projects.wordle.domain.enums.TypeLanguages

fun Context.getInitialAppLocale(): String {
    val systemLang = resources.configuration.locales[0].language
    return TypeLanguages.fromCode(systemLang).code
}