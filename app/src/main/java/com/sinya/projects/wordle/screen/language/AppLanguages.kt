package com.sinya.projects.wordle.screen.language

object AppLanguages {
    val supported = listOf(
        LangItem("en", "English", "English"),
        LangItem("ru", "Русский", "Russian"),
        // и т.д.
    )

    fun getByCode(code: String): String? = supported.find { it.code == code }?.nativeName

    fun getCode(code: String): String? = supported.find { it.code == code }?.code
}