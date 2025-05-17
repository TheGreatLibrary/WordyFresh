package com.sinya.projects.wordle.ui.components

import com.sinya.projects.wordle.domain.model.data.LangItem

object AppLanguages {
    val supported = listOf(
        LangItem("en", "English", "English"),
        LangItem("ru", "Русский", "Russian"),
        // и т.д.
    )

    fun getByCode(code: String): String? = supported.find { it.code == code }?.nativeName
}