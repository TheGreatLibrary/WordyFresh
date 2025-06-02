package com.sinya.projects.wordle.utils

import android.annotation.SuppressLint

@SuppressLint("DefaultLocale")
fun absTime(times: Long, countGame: Int): String {
    if (countGame == 0) return "00:00" // избегаем деления на 0

    val averageSeconds = times / countGame

    val hours = averageSeconds / 3600
    val minutes = (averageSeconds % 3600) / 60
    val seconds = averageSeconds % 60

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}