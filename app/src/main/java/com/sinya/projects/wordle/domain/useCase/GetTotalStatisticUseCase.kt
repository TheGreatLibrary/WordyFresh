package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.data.local.database.entity.OfflineStatistic
import com.sinya.projects.wordle.domain.enums.GameMode
import com.sinya.projects.wordle.domain.repository.StatisticRepository
import jakarta.inject.Inject

class GetTotalStatisticUseCase @Inject constructor(
    private val repository: StatisticRepository
) {
    operator fun invoke(list: List<OfflineStatistic>, mode: GameMode): Result<OfflineStatistic> {
        return try {
            Result.success(repository.getTotalStatistic(list, mode))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}