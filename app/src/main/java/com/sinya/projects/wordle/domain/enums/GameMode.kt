package com.sinya.projects.wordle.domain.enums

import androidx.annotation.StringRes
import com.sinya.projects.wordle.R

enum class GameMode(
    val id: Int,
    @StringRes val res: Int
) {
    ALL(-2, R.string.all_modes),
    SAVED(-1, R.string.saved_game),
    NORMAL(0, R.string.classic_mode),
    HARD(1, R.string.hard_m),
    RANDOM(2, R.string.random_m),
    FRIENDLY(3, R.string.friend_m);

    val isForStats: Boolean
        get() = this != SAVED

    companion object {
        fun fromCode(code: Int): GameMode =
            entries.find { it.id == code } ?: NORMAL
    }
}