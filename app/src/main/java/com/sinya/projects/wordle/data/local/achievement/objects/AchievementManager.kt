package com.sinya.projects.wordle.data.local.achievement.objects

import com.sinya.projects.wordle.data.local.achievement.AchievementChecker
import com.sinya.projects.wordle.data.local.achievement.AchievementTrigger
import com.sinya.projects.wordle.data.local.achievement.UserStats
import com.sinya.projects.wordle.data.local.achievement.interfaces.AchievementRepository

object AchievementManager {
    private lateinit var checker: AchievementChecker

    fun init(repo: AchievementRepository) {
        checker = AchievementChecker(repo)
    }

    suspend fun onTrigger(trigger: AchievementTrigger, stats: UserStats) {
        checker.checkAndUnlock(trigger, stats)
    }
}