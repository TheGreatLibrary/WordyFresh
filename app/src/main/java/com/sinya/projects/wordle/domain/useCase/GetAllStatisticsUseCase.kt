package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.data.local.database.entity.OfflineStatistic
import com.sinya.projects.wordle.domain.repository.StatisticRepository
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetAllStatisticsUseCase @Inject constructor(
    private val repository: StatisticRepository
) {
    suspend operator fun invoke(): Result<List<OfflineStatistic>> = withContext(Dispatchers.IO) {
        try {
            Result.success(repository.getAllStatistic())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}