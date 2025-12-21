package com.sinya.projects.wordle.data.local.achievement

import com.sinya.projects.wordle.domain.enums.GameMode

sealed interface AchievementTrigger {
    data class GameFinishedTrigger(
        val isWin: Boolean,
        val mode: GameMode,
        val word: String,
        val lang: String,
        val attempts: Int,
        val timeSeconds: Long,
    ) : AchievementTrigger
    data object AccountRegistered : AchievementTrigger
    data object SupportMessageSent : AchievementTrigger
}