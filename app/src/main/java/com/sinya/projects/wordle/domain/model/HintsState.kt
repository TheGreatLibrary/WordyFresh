package com.sinya.projects.wordle.domain.model

import com.sinya.projects.wordle.utils.HintsConfig
import kotlin.time.Duration

data class HintsState(
    val available: Int,
    val maxHints: Int = HintsConfig.MAX_HINTS,
    val usedThisRound: Int,
    val canUseThisRound: Boolean = usedThisRound < HintsConfig.MAX_HINTS_PER_ROUND,
    val nextRestoreIn: Duration? // null если уже макс
)