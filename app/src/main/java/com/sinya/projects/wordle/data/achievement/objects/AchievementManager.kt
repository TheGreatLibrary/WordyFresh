package com.sinya.projects.wordle.data.achievement.objects

import com.sinya.projects.wordle.data.achievement.AchievementChecker
import com.sinya.projects.wordle.data.achievement.AchievementTrigger
import com.sinya.projects.wordle.data.achievement.UserStats
import com.sinya.projects.wordle.data.achievement.interfaces.AchievementRepository

object AchievementManager {
    private lateinit var checker: AchievementChecker

    fun init(repo: AchievementRepository) {
        checker = AchievementChecker(repo)
    }

    suspend fun onTrigger(trigger: AchievementTrigger, stats: UserStats) {
        checker.checkAndUnlock(trigger, stats)
    }
}