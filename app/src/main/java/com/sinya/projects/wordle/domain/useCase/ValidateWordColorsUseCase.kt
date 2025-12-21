package com.sinya.projects.wordle.domain.useCase

import androidx.compose.ui.graphics.Color
import com.sinya.projects.wordle.ui.theme.gray600
import com.sinya.projects.wordle.ui.theme.green800
import com.sinya.projects.wordle.ui.theme.yellow
import jakarta.inject.Inject

class ValidateWordColorsUseCase @Inject constructor() {
    operator fun invoke(enteredWord: String, hiddenWord: String): List<Color> {
        val len = enteredWord.length
        val colors = MutableList(len) { gray600 }
        val usedIndices = BooleanArray(len)

        // Точные совпадения (зеленый)
        for (i in 0 until len) {
            if (enteredWord[i] == hiddenWord[i]) {
                colors[i] = green800
                usedIndices[i] = true
            }
        }

        // Буквы не на месте (желтый)
        for (i in 0 until len) {
            if (colors[i] == green800) continue

            for (j in hiddenWord.indices) {
                if (!usedIndices[j] && enteredWord[i] == hiddenWord[j]) {
                    colors[i] = yellow
                    usedIndices[j] = true
                    break
                }
            }
        }

        return colors
    }
}