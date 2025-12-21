package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.data.local.database.entity.OfflineStatistic
import com.sinya.projects.wordle.domain.repository.StatisticRepository
import jakarta.inject.Inject

class GetMergedSummaryUseCase @Inject constructor(
    private val repository: StatisticRepository
) {
    suspend operator fun invoke(): Result<OfflineStatistic> {
        return try {
            Result.success(repository.getMergedSummary())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
