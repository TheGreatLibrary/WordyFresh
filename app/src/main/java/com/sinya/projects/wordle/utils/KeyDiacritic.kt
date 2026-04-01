package com.sinya.projects.wordle.utils

import androidx.compose.runtime.toMutableStateList
import com.sinya.projects.wordle.domain.enums.TypeLanguages
import com.sinya.projects.wordle.domain.model.Key

object GeneratorKeyboardLayout {

    fun getKeyboard(lang: String, code: Int): List<List<Key>> {
        val layout = when (lang) {
            TypeLanguages.RU.code -> getRussianKeyboard(code)
            TypeLanguages.CS.code -> return buildKeyboardState(getCzechKeyboard(code), czechDiacritics)
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

    private fun getCzechKeyboard(code: Int): List<String> {
        return when (code) {
            0 -> listOf(
                "QWERTYUIOP",
                "ASDFGHJKLĚ",
                "<ZXCVBNMŮ>"
            )
            1 -> listOf(
                "QWERTYUIOP",
                "ASDFGHJKL<",
                "ZXCVBNMĚŮ>"
            )
            2 -> listOf(
                "QWERTYUIOP",
                "ASDFGHJKLĚ",
                ">ZXCVBNMŮ<"
            )
            3 -> listOf(
                "QWERTYUIOP",
                "ASDFGHJKLĚ",
                "ZXCVBNMŮ<",
                ">"
            )
            else -> getCzechKeyboard(0)
        }
    }

    private val czechDiacritics: Map<Char, Char> = mapOf(
        'A' to 'Á',
        'C' to 'Č',
        'D' to 'Ď',
        'E' to 'É',
        'I' to 'Í',
        'N' to 'Ň',
        'O' to 'Ó',
        'R' to 'Ř',
        'S' to 'Š',
        'T' to 'Ť',
        'U' to 'Ú',
        'Y' to 'Ý',
        'Z' to 'Ž'
    )

//    private val russianDiacritics: Map<Char, Char> = mapOf(
//        'Ь' to 'Ъ'
//    )
}

