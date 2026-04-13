package com.sinya.projects.wordle.utils

import com.sinya.projects.wordle.presentation.game.finishSheet.StatDiff

fun calculateTimeDiff(values: List<Int>): StatDiff {
    val old = values.getOrElse(0) { 0 }
    val new = values.getOrElse(1) { 0 }
    val diff = new - old

    return StatDiff(
        value = formatTime(new),
        difference = "${if (diff <= 0) "" else "+"}${formatTime(diff)}",
        isPositive = if (diff == 0) null else diff <= 0
    )
}

private fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return if (mins > 0) "${mins}:${secs.toString().padStart(2, '0')}" else "${secs}с"
}