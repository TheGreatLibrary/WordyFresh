package com.sinya.projects.wordle.domain.model

data class DictionaryItem(
    val id: Int,
    val word: String,
    val description: String,
    val length: Int?,
    val lang: String?,
    val isLoading: Boolean = false
)

fun List<DictionaryItem>.filterByLength(length: String?): List<DictionaryItem> {
    return this
        .filter { dictionaryItem ->
            length.isNullOrEmpty() || dictionaryItem.length.toString() == length || (length == "no one" && dictionaryItem.length == null)
        }
        .sortedByDescending { it.word }
}

val List<DictionaryItem>.lengths: List<String?>
    get() = this
        .map { it.length.toString() }
        .distinct()
        .sortedBy { it.toIntOrNull() }

fun List<DictionaryItem>.filterByLanguage(lang: String?): List<DictionaryItem> {
    return this
        .filter { dictionaryItem ->
            lang.isNullOrEmpty() || dictionaryItem.lang.toString() == lang || (lang == "no one" && dictionaryItem.lang == null)
        }
        .sortedByDescending { it.word }
}

val List<DictionaryItem>.languages: List<String?>
    get() = this
        .map { it.lang }
        .distinct()
        .sortedBy { it }