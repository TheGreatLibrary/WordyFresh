package com.sinya.projects.wordle.screen.keyboard

data class KeyboardItem(
    val code: Int,         // 0, 1, 2....
    val modeName: Int,   // R.string.name
    val modeDescription: Int,
    val previewRes: Int
)