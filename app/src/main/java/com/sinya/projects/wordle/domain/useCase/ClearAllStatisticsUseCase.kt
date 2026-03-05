package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.domain.repository.StatisticRepository
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ClearAllStatisticsUseCase @Inject constructor(
    private val repository: StatisticRepository
) {
    suspend operator fun invoke(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            repository.clearAllStatistics()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

