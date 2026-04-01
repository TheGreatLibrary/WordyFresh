package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.domain.repository.AchievementRepository
import jakarta.inject.Inject

class ClearAllAchievementUseCase @Inject constructor(
    private val repository: AchievementRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.clearAllAchievement()
    }
}