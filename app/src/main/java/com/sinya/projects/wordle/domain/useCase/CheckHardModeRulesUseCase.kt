package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.ui.theme.green800
import com.sinya.projects.wordle.ui.theme.yellow
import jakarta.inject.Inject

class CheckHardModeRulesUseCase @Inject constructor(
    private val validateWordColorsUseCase: ValidateWordColorsUseCase
) {
    operator fun invoke(
        enteredWord: String,
        previousWord: String,
        hiddenWord: String,
        wordLength: Int
    ): HardModeResult {
        val lastColorArr = validateWordColorsUseCase(previousWord, hiddenWord)
        val requiredLetters = mutableSetOf<Char>()

        for (i in 0 until wordLength) {
            val lastColor = lastColorArr[i]
            val prevChar = previousWord[i]

            if (lastColor == green800 && enteredWord[i] != prevChar) {
                return HardModeResult.ExactPositionError(prevChar, i + 1)
            }

            if (lastColor == green800 || lastColor == yellow) {
                requiredLetters.add(prevChar)
            }
        }

        for (char in requiredLetters) {
            if (!enteredWord.contains(char)) {
                return HardModeResult.LetterRequiredError(char)
            }
        }

        return HardModeResult.Valid
    }

    sealed class HardModeResult {
        data object Valid : HardModeResult()
        data class ExactPositionError(val char: Char, val position: Int) : HardModeResult()
        data class LetterRequiredError(val char: Char) : HardModeResult()
    }
}