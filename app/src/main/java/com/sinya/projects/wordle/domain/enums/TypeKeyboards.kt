package com.sinya.projects.wordle.domain.enums

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.sinya.projects.wordle.R

enum class TypeKeyboards(
    val code: Int,
    @StringRes val title: Int,
    @DrawableRes val res: Int
) {
    WORDLE(0,  R.string.keyboard_wordle, R.drawable.keyboard_wordle),
    CLASSIC(1,  R.string.keyboard_classic, R.drawable.keyboard_classic),
    REVERSE(2,  R.string.keyboard_reverse, R.drawable.keyboard_reverse),
    BIG_ENTER(3,  R.string.keyboard_big_enter, R.drawable.keyboard_big_space);

    companion object {
        fun fromCode(code: Int): TypeKeyboards = entries.find { it.code == code } ?: WORDLE
    }
}