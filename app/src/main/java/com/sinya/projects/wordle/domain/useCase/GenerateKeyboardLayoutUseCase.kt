package com.sinya.projects.wordle.domain.useCase

import androidx.compose.runtime.toMutableStateList
import com.sinya.projects.wordle.domain.model.Key
import jakarta.inject.Inject

class GenerateKeyboardLayoutUseCase @Inject constructor() {
    operator fun invoke(lang: String, code: Int): List<List<Key>> {
        val layout = when (lang) {
            "ru" -> getRussianKeyboard(code)
            else -> getEnglishKeyboard(code)
        }

        return layout.map { row ->
            row.map { Key(it) }.toMutableStateList()
        }.toMutableStateList()
    }

    private fun getRussianKeyboard(code: Int): List<String> {
        return when (code) {
            0 -> listOf("ЙЦУКЕНГШЩЗХЪ", "ФЫВАПРОЛДЖЭ", "<ЯЧСМИТЬБЮ>")
            1 -> listOf("ЙЦУКЕНГШЩЗХЪ", "ФЫВАПРОЛДЖЭ<", "ЯЧСМИТЬБЮ>")
            2 -> listOf("ЙЦУКЕНГШЩЗХЪ", "ФЫВАПРОЛДЖЭ", ">ЯЧСМИТЬБЮ<")
            3 -> listOf("ЙЦУКЕНГШЩЗХЪ", "ФЫВАПРОЛДЖЭ", "ЯЧСМИТЬБЮ<", ">")
            else -> getRussianKeyboard(0)
        }
    }

    private fun getEnglishKeyboard(code: Int): List<String> {
        return when (code) {
            0 -> listOf("QWERTYUIOP", "ASDFGHJKL", "<ZXCVBNM>")
            1 -> listOf("QWERTYUIOP", "ASDFGHJKL<", "ZXCVBNM>")
            2 -> listOf("QWERTYUIOP", "ASDFGHJKL", ">ZXCVBNM<")
            3 -> listOf("QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM<", ">")
            else -> getEnglishKeyboard(0)
        }
    }
}