package com.sinya.projects.wordle.utils

import com.sinya.projects.wordle.domain.error.DefinitionNotFoundException
import com.sinya.projects.wordle.domain.error.NoInternetException
import com.sinya.projects.wordle.domain.error.WordNotFoundException

object ErrorLocalizer {
    fun getMessage(throwable: Throwable, lang: String): String {
        return when (lang.lowercase()) {
            "ru" -> getRussian(throwable)
            "cs" -> getCzech(throwable)
            else -> getEnglish(throwable) // Дефолт
        }
    }

    private fun getRussian(t: Throwable) = when (t) {
        is DefinitionNotFoundException -> "Определение не найдено."
        is NoInternetException -> "Нет интернета. Ограниченный доступ."
        is WordNotFoundException -> "Слово не найдено."
        else -> "Что-то пошло не так."
    }

    private fun getCzech(t: Throwable) = when (t) {
        is DefinitionNotFoundException -> "Definice nenalezena."
        is NoInternetException -> "Žádný internet. Omezený přístup."
        is WordNotFoundException -> "Slovo nenalezeno."
        else -> "Něco se pokazilo."
    }

    private fun getEnglish(t: Throwable) = when (t) {
        is DefinitionNotFoundException -> "Definition not found."
        is NoInternetException -> "No internet. Limited access."
        is WordNotFoundException -> "Word not found."
        else -> "Something went wrong."
    }
}