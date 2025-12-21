package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.data.local.database.entity.OfflineStatistic
import com.sinya.projects.wordle.domain.repository.StatisticRepository
import jakarta.inject.Inject

class UpdateStatisticUseCase @Inject constructor(
    private val repository: StatisticRepository
) {
    suspend operator fun invoke(updated: OfflineStatistic): Result<Unit> {
        return try {
            repository.updateStatistic(updated)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}