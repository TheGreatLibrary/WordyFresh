package com.sinya.projects.wordle.utils

import com.sinya.projects.wordle.domain.model.Key

fun buildKeyboardState(
    rows: List<String>,
    diacritics: Map<Char, Char> = emptyMap()
): List<List<Key>> {
    return rows.map { row ->
        row.map { char ->
            Key(
                char = char,
                diacriticChar = diacritics[char]
            )
        }
    }
}