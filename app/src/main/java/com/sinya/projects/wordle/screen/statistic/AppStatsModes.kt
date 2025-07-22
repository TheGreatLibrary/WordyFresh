package com.sinya.projects.wordle.screen.statistic

object AppStatsModes {
    val supported = listOf(
        ModeItem(id = -1, name =  "all_modes"),
        ModeItem(id = 0, name = "classic_mode"),
        ModeItem(id = 1, name = "hard_m"),
        ModeItem(id = 2, name = "random_m"),
        ModeItem(id = 3, name = "friend_m")
    )

    fun getByCode(code: Int): ModeItem? = supported.find { it.id == code }
}