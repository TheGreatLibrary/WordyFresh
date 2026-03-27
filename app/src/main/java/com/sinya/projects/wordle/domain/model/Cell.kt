package com.sinya.projects.wordle.domain.model

import com.sinya.projects.wordle.domain.enums.GameColors
import kotlinx.serialization.Serializable

@Serializable
data class Cell(
    var letter: String = "",
    var backgroundColor: GameColors = GameColors.DEFAULT_CELL,
    var hint: String = ""
)

fun List<Cell>.updateColor(index: Int, color: GameColors): List<Cell> {
    if (index !in this.indices) return this

    return toMutableList().apply {
        this[index] = this[index].copy(backgroundColor = color)
    }
}

fun List<Cell>.updateHint(index: Int, hint: String): List<Cell> {
    if (index !in this.indices) return this
    return toMutableList().apply {
        this[index] = this[index].copy(hint = hint)
    }
}

fun List<Cell>.clearHints(row: Int, wordLength: Int): List<Cell> {
    var grid = this
    for (col in 0 until wordLength) {
        grid = grid.updateHint(row * wordLength + col, "")
    }
    return grid
}

fun List<Cell>.updateText(index: Int, text: String): List<Cell> {
    if (index !in this.indices) return this
    if (this[index].backgroundColor == GameColors.GREEN) return this

    return toMutableList().apply {
        this[index] = this[index].copy(letter = text)
    }
}

fun List<Cell>.getWord(row: Int, wordLength: Int): String {
    val start = row * wordLength
    return subList(start, start + wordLength)
        .joinToString("") { it.letter }
}

fun List<Cell>.toEmojiGridFromULong(wordLength: Int): String {
    return this.chunked(wordLength).joinToString("\n") { row ->
        row.joinToString("") { colorValue ->
            when (colorValue.backgroundColor) {
                GameColors.GREEN -> "🟩"  // зелёный
                GameColors.YELLOW -> "🟨"  // жёлтый
                GameColors.GRAY -> "⬜" // серый/чёрный
                else -> "⬛"
            }
        }
    }
}

