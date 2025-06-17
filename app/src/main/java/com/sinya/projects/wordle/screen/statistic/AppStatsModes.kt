package com.sinya.projects.wordle.screen.statistic

import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.domain.model.data.ModeItem

object AppStatsModes {
    val supported = listOf(
        ModeItem(R.string.all_modes, "all"),
        ModeItem(R.string.classic_mode, "12f9d2ce-1234-4321-aaaa-000000000001"),
        ModeItem(R.string.hard_m, "12f9d2ce-1234-4321-aaaa-000000000002"),
        ModeItem(R.string.random_m, "12f9d2ce-1234-4321-aaaa-000000000003"),
        ModeItem(R.string.friend_m, "12f9d2ce-1234-4321-aaaa-000000000004")
    )

    fun getByCode(code: String): ModeItem? = supported.find { it.uuid == code }
}