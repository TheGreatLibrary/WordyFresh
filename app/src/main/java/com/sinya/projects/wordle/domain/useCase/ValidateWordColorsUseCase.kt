package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.domain.enums.GameColors
import jakarta.inject.Inject

class ValidateWordColorsUseCase @Inject constructor() {
    operator fun invoke(enteredWord: String, hiddenWord: String): List<GameColors> {
        val len = enteredWord.length
        val colors = MutableList(len) { GameColors.GRAY }
        val usedIndices = BooleanArray(len)

        // Точные совпадения (зеленый)
        for (i in 0 until len) {
            if (enteredWord[i] == hiddenWord[i]) {
                colors[i] = GameColors.GREEN
                usedIndices[i] = true
            }
        }

        // Буквы не на месте (желтый)
        for (i in 0 until len) {
            if (colors[i] == GameColors.GREEN) continue

            for (j in hiddenWord.indices) {
                if (!usedIndices[j] && enteredWord[i] == hiddenWord[j]) {
                    colors[i] = GameColors.YELLOW
                    usedIndices[j] = true
                    break
                }
            }
        }

        return colors
    }
}