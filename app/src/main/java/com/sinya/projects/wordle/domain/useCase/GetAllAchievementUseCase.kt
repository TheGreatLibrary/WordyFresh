package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.domain.repository.AchievementRepository
import com.sinya.projects.wordle.domain.model.AchieveItem
import jakarta.inject.Inject

class GetAllAchievementUseCase @Inject constructor(
    private val repository: AchievementRepository
) {
    suspend operator fun invoke(): Result<List<AchieveItem>> {
        return try {
            Result.success(repository.getAllAchievements())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}