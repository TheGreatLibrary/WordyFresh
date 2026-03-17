package com.sinya.projects.wordle.data.local.achievement

import com.sinya.projects.wordle.domain.enums.GameMode

sealed interface AchievementTrigger {
    data class GameFinishedTrigger(
        val isWin: Boolean,
        val mode: GameMode,
        val word: String,
        val attemptsWords: List<String>,
        val lang: String,
        val length: Int,
        val rowAttempts: Int,
        val timeSeconds: Int,
    ) : AchievementTrigger
    data object AccountRegistered : AchievementTrigger
    data object SupportMessageSent : AchievementTrigger
}