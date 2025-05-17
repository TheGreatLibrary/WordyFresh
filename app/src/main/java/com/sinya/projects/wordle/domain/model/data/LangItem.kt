package com.sinya.projects.wordle.domain.model.data

data class LangItem(
    val code: String,         // "ru", "en", etc.
    val nativeName: String,   // "Русский"
    val englishName: String   // "Russian"
)