package com.sinya.projects.wordle.screen.theme

import com.sinya.projects.wordle.R

object AppThemes {
    val supported = listOf(
        ThemeItem(false, R.string.light),
        ThemeItem(true, R.string.dark)
    )

    fun getByCode(isDark: Boolean): ThemeItem? = supported.find { it.isDark == isDark }
}