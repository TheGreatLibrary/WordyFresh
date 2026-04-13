package com.sinya.projects.wordle.utils

import com.sinya.projects.wordle.presentation.game.finishSheet.StatDiff

fun calculateIntDiff(values: List<Int>): StatDiff {
    val old = values.getOrElse(0) { 0 }
    val new = values.getOrElse(1) { 0 }
    val diff = new - old

    return StatDiff(
        value = new.toString(),
        difference = "${if (diff >= 0) "+" else ""}$diff",
        isPositive = if (diff == 0) null else diff >= 0
    )
}
