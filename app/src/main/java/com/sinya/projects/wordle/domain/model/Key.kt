package com.sinya.projects.wordle.domain.model

import androidx.compose.ui.graphics.Color
import com.sinya.projects.wordle.ui.theme.gray100
import kotlinx.serialization.Serializable

@Serializable
data class Key(
    var char: Char,
    var color: ULong = gray100.value
)

fun List<List<Key>>.updateColor(char: Char, color: Color): List<List<Key>> {
    return map { row ->
        row.map { key ->
            if (key.char == char) key.copy(color = color.value) else key
        }
    }
}


