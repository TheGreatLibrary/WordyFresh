package com.sinya.projects.wordle.data.local.achievement

import com.sinya.projects.wordle.data.local.achievement.interfaces.AchievementCondition
import com.sinya.projects.wordle.data.local.entity.OfflineStatistic
import com.sinya.projects.wordle.screen.game.model.GameMode

sealed class AchievementTrigger {
    data class GameFinishedTrigger(
        val isWin: Boolean,
        val mode: GameMode,
        val word: String,
        val lang: String,
        val attempts: Int,
        val timeSeconds: Long,
    ) : AchievementTrigger()
    data object AccountRegistered : AchievementTrigger()
    data object SupportMessageSent : AchievementTrigger()
}

data class Achievement(
    val id: Int,
    val title: String,
    val condition: String,
    val count: Int,
    val maxCount: Int,
    val isSatisfied: AchievementCondition
)

data class UserStats(
    val statistic: OfflineStatistic,
)

