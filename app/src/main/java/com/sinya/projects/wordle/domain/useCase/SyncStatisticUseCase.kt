package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.domain.repository.StatisticRepository
import jakarta.inject.Inject

class SyncStatisticUseCase @Inject constructor(
    private val repository: StatisticRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.syncFromSupabase()
    }
}