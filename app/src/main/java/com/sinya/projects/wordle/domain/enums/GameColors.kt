package com.sinya.projects.wordle.domain.enums

import com.sinya.projects.wordle.ui.theme.gray100
import com.sinya.projects.wordle.ui.theme.gray30
import com.sinya.projects.wordle.ui.theme.gray600
import com.sinya.projects.wordle.ui.theme.green800
import com.sinya.projects.wordle.ui.theme.yellow

enum class GameColors(val value: ULong) {
    GREEN(green800.value),
    YELLOW(yellow.value),
    GRAY(gray600.value),
    DEFAULT_CELL(gray30.value),
    DEFAULT_KEY(gray100.value)
}