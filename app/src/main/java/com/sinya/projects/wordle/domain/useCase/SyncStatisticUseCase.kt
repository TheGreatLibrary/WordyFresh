package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.domain.repository.StatisticRepository
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncStatisticUseCase @Inject constructor(
    private val repository: StatisticRepository
) {
    suspend operator fun invoke(): Result<Unit> = withContext(Dispatchers.IO) {
        repository.syncFromSupabase()
    }
}