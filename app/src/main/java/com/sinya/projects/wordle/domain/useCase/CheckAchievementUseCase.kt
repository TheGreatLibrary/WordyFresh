package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.data.local.achievement.AchievementEvent
import com.sinya.projects.wordle.data.local.achievement.AchievementEventBus
import com.sinya.projects.wordle.data.local.achievement.AchievementTrigger
import com.sinya.projects.wordle.data.local.achievement.AchievementId
import com.sinya.projects.wordle.data.local.achievement.ConditionFactory
import com.sinya.projects.wordle.domain.enums.TypeAchievement
import com.sinya.projects.wordle.domain.repository.AchievementRepository
import jakarta.inject.Inject

class CheckAchievementUseCase @Inject constructor(
    private val achievementRepository: AchievementRepository,
    private val achievementEventBus: AchievementEventBus
) {
    suspend operator fun invoke(trigger: AchievementTrigger): Result<List<AchievementEvent>> {
        return try {
            val achievements = achievementRepository.getAllAchievements()
            val events = mutableListOf<AchievementEvent>()

            achievements.forEach { achieve ->
                val achievementId = AchievementId.fromId(achieve.id)
                val condition = ConditionFactory.create(achievementId)
                val typeAchievement = ConditionFactory.getTypeAchievement(achievementId)
                val isSatisfied = condition.isSatisfied(trigger)

                val wasUnlocked = achieve.isUnlocked
                val oldCount = achieve.count

                when {
                    typeAchievement == TypeAchievement.STREAK -> {
                        if (isSatisfied) {
                            achievementRepository.unlockIncrement(achieve.id)
                            val updated = achieve.copy(count = oldCount + 1)

                            if (!wasUnlocked && updated.isUnlocked) {
                                // Разблокировали!
                                events.add(AchievementEvent.Unlocked(updated))
                                achievementEventBus.emit(AchievementEvent.Unlocked(updated))
                            } else if (!updated.isUnlocked) {
                                // Прогресс изменился, но ещё не разблокировано
                                events.add(AchievementEvent.ProgressUpdated(updated))
                                achievementEventBus.emit(AchievementEvent.ProgressUpdated(updated))
                            }
                        } else {
                            // Сброс серии — тоже прогресс (откат)
                            if (oldCount > 0) {
                                achievementRepository.resetCount(achieve.id)
                                val updated = achieve.copy(count = 0)
                                events.add(AchievementEvent.ProgressUpdated(updated))
                                achievementEventBus.emit(AchievementEvent.ProgressUpdated(updated))
                            }
                        }
                    }

                    !wasUnlocked && isSatisfied -> {
                        achievementRepository.unlockIncrement(achieve.id)
                        val updated = achieve.copy(count = oldCount + 1)

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

            Result.success(events)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}