package com.sinya.projects.wordle.ui.components

import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.domain.model.data.KeyboardItem
import com.sinya.projects.wordle.domain.model.data.LangItem

object AppKeyboards {
    val supported = listOf(
        KeyboardItem(0,  R.string.keyboard_wordle,  R.string.keyboard_wordle, R.drawable.keyboard_wordle),
        KeyboardItem(1,  R.string.keyboard_classic,  R.string.keyboard_wordle, R.drawable.keyboard_classic),
        KeyboardItem(2,  R.string.keyboard_reverse,  R.string.keyboard_wordle, R.drawable.keyboard_reverse),
        KeyboardItem(3,  R.string.keyboard_big_enter,  R.string.keyboard_wordle, R.drawable.keyboard_big_space),
    )

    fun getByCode(code: Int): KeyboardItem? = supported.find { it.code == code }
}