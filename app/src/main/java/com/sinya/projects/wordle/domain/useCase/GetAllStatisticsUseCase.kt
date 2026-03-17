package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.domain.model.StatAggregated
import com.sinya.projects.wordle.domain.repository.StatisticRepository
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetAllStatisticsUseCase @Inject constructor(
    private val repository: StatisticRepository
) {
    suspend operator fun invoke(): Result<List<StatAggregated>> = withContext(Dispatchers.IO) {
        try {
            Result.success(repository.getAggregatedAll())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}