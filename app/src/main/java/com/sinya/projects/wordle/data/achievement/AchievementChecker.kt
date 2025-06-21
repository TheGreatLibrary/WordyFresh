package com.sinya.projects.wordle.data.achievement

import com.sinya.projects.wordle.data.achievement.interfaces.AchievementRepository

class AchievementChecker(private val repo: AchievementRepository) {
    private val streakKeys = setOf(
        "achieve_cond_streak",   // победная серия
        "achieve_cond_genius"    // серия поражений
    )

    suspend fun checkAndUnlock(trigger: AchievementTrigger, stats: UserStats) {
        val all = repo.getAll()

        all.forEach { ach ->
            when {
                ach.condition in streakKeys -> {
                    if (ach.isSatisfied.isSatisfied(trigger, stats)) {
                        repo.unlockIncrement(ach.id)
                    } else {
                        repo.resetCount(ach.id)
                    }
                } // серийные достижения
                ach.count < ach.maxCount && ach.isSatisfied.isSatisfied(trigger, stats) -> {
                    repo.unlockIncrement(ach.id)
                } // обычные накопительные
                else -> Unit // заглушка
            }
        }
    }
}