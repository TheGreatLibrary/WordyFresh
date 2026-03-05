package com.sinya.projects.wordle.domain.model

import androidx.compose.ui.graphics.Color
import com.sinya.projects.wordle.ui.theme.gray30
import com.sinya.projects.wordle.ui.theme.gray600
import com.sinya.projects.wordle.ui.theme.green800
import com.sinya.projects.wordle.ui.theme.yellow
import kotlinx.serialization.Serializable

@Serializable
data class Cell(
    var letter: String = "",
    var backgroundColor: ULong = gray30.value
)

fun List<Cell>.updateColor(index: Int, color: Color): List<Cell> {
    if (index !in this.indices) return this

    return toMutableList().apply {
        this[index] = this[index].copy(backgroundColor = color.value)
    }
}

fun List<Cell>.updateText(index: Int, text: String): List<Cell> {
    if (index !in this.indices) return this

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
            when (Color(colorValue.backgroundColor)) {
                green800 -> "🟩"  // зелёный
                yellow -> "🟨"  // жёлтый
                gray600 -> "⬜" // серый/чёрный
                else -> "⬛"
            }
        }
    }
}

