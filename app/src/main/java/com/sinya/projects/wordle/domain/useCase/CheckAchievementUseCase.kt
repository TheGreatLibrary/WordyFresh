package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.data.local.achievement.AchievementEvent
import com.sinya.projects.wordle.data.local.achievement.AchievementEventBus
import com.sinya.projects.wordle.data.local.achievement.AchievementTrigger
import com.sinya.projects.wordle.data.local.achievement.AchievementId
import com.sinya.projects.wordle.data.local.achievement.ConditionFactory
import com.sinya.projects.wordle.domain.enums.TypeAchievement
import com.sinya.projects.wordle.domain.repository.AchievementRepository
import com.sinya.projects.wordle.domain.repository.StatisticRepository
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CheckAchievementUseCase @Inject constructor(
    private val achievementRepository: AchievementRepository,
    private val achievementEventBus: AchievementEventBus,
    private val statisticRepository: StatisticRepository
) {
    suspend operator fun invoke(trigger: AchievementTrigger, lang: String): Result<List<AchievementEvent>> = withContext(Dispatchers.IO) {
        try {
            val achievements = achievementRepository.getAllAchievements(lang).getOrNull() ?: emptyList()
            val events = mutableListOf<AchievementEvent>()
            val currentIsWin = (trigger as? AchievementTrigger.GameFinishedTrigger)?.isWin ?: false

            achievements.forEach { achieve ->
                val achievementId = AchievementId.fromId(achieve.id)
                val condition = ConditionFactory.create(achievementId)
                val typeAchievement = ConditionFactory.getTypeAchievement(achievementId)
                val isSatisfied = condition.isSatisfied(trigger)

                val wasUnlocked = achieve.isUnlocked
                val oldCount = achieve.count


                when {
                    typeAchievement == TypeAchievement.STREAK -> {
                        if (wasUnlocked) return@forEach

                        val realStreak = when (achievementId) {
                            AchievementId.WIN_STREAK_10 -> statisticRepository.getCurrentStreak(isWin = true, currentIsWin)
                            AchievementId.LOSE_STREAK_5 -> statisticRepository.getCurrentStreak(isWin = false, currentIsWin)
                            else -> 0
                        }

                        achievementRepository.setOfflineCount(achieve.id, realStreak)
                        achievementRepository.resetSyncCount(achieve.id) // обнуляем sync

                        val updated = achieve.copy(count = realStreak)

                        when {
                            !wasUnlocked && updated.isUnlocked -> {
                                events.add(AchievementEvent.Unlocked(updated))
                                achievementEventBus.emit(AchievementEvent.Unlocked(updated))
                            }
                            !updated.isUnlocked && realStreak != oldCount -> {
                                events.add(AchievementEvent.ProgressUpdated(updated))
                                achievementEventBus.emit(AchievementEvent.ProgressUpdated(updated))
                            }
                        }
                    }

                    !wasUnlocked && isSatisfied -> {
                        val increment = condition.getIncrement(trigger)
                        achievementRepository.unlockIncrement(achieve.id, increment)
                        val updated = achieve.copy(count = oldCount + increment)

                        if (updated.isUnlocked) {
                            events.add(AchievementEvent.Unlocked(updated))
                            achievementEventBus.emit(AchievementEvent.Unlocked(updated))
                        } else {
                            events.add(AchievementEvent.ProgressUpdated(updated))
                            achievementEventBus.emit(AchievementEvent.ProgressUpdated(updated))
                        }
                    }
                }
            }

            val freshAchievements = achievementRepository.getAllAchievements(lang).getOrNull() ?: emptyList()
            val platinumAchieve = freshAchievements.firstOrNull { it.id == AchievementId.PLATINUM.id }

            if (platinumAchieve != null && !platinumAchieve.isUnlocked) {
                val allUnlocked = freshAchievements
                    .filter { it.id != AchievementId.PLATINUM.id }
                    .all { it.isUnlocked }

                if (allUnlocked) {
                    achievementRepository.unlockIncrement(platinumAchieve.id, 1)
                    val updated = platinumAchieve.copy(count = 1)
                    events.add(AchievementEvent.Unlocked(updated))
                    achievementEventBus.emit(AchievementEvent.Unlocked(updated))
                }
            }

            Result.success(events)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}