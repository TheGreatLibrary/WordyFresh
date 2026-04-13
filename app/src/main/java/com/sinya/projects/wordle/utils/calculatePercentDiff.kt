package com.sinya.projects.wordle.utils

import com.sinya.projects.wordle.presentation.game.finishSheet.StatDiff

fun calculatePercentDiff(percentWin: List<Float?>): StatDiff {
    val old = percentWin[0] ?: 0f
    val new = percentWin[1] ?: 0f
    val diff = ((new - old) * 100).toInt()

    return StatDiff(
        value = "${(new * 100).toInt()}%",
        difference = "${if (diff >= 0) "+" else ""}$diff%",
        isPositive = if (diff == 0) null else diff >= 0
    )
}