package com.sinya.projects.wordle.screen.statistic

import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.domain.model.data.Mode

object AppStatsModes {
    val supported = listOf(
        Mode(R.string.all_modes, "all"),
        Mode(R.string.classic_mode, "12f9d2ce-1234-4321-aaaa-000000000001"),
        Mode(R.string.hard_m, "12f9d2ce-1234-4321-aaaa-000000000002"),
        Mode(R.string.random_m, "12f9d2ce-1234-4321-aaaa-000000000003"),
        Mode(R.string.friend_m, "12f9d2ce-1234-4321-aaaa-000000000004")
    )

    fun getByCode(code: String): Mode? = supported.find { it.uuid == code }
}