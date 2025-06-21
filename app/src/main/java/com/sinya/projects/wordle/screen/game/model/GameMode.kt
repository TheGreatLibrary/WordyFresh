package com.sinya.projects.wordle.screen.game.model

enum class GameMode(val code: Int) {
    SAVED(-1),
    NORMAL(0),
    HARD(1),
    FRIENDLY(2),
    RANDOM(3);

    companion object {
        fun fromCode(code: Int): GameMode =
            entries.find { it.code == code } ?: NORMAL
    }
}