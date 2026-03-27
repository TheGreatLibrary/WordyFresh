package com.sinya.projects.wordle.domain.model

import com.sinya.projects.wordle.domain.enums.GameColors
import kotlinx.serialization.Serializable

@Serializable
data class Key(
    var char: Char,
    var color: GameColors = GameColors.DEFAULT_KEY
)

fun List<List<Key>>.updateColor(char: Char, color: GameColors): List<List<Key>> {
    return map { row ->
        row.map { key ->
            if (key.char == char) key.copy(color = color) else key
        }
    }
}


