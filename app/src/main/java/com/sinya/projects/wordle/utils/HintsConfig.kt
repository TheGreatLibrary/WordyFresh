package com.sinya.projects.wordle.utils

import kotlin.time.Duration.Companion.hours

object HintsConfig {
    const val MAX_HINTS = 3
    val RESTORE_INTERVAL = 1.hours
    const val MAX_HINTS_PER_ROUND = 2
}

