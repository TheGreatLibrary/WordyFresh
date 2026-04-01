package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.data.local.database.entity.OfflineStatistics
import com.sinya.projects.wordle.domain.repository.StatisticRepository
import jakarta.inject.Inject

class InsertStatisticUseCase @Inject constructor(
    private val repository: StatisticRepository
) {
    suspend operator fun invoke(updated: OfflineStatistics): Result<Unit> {
        return repository.insertGame(updated)
    }
}