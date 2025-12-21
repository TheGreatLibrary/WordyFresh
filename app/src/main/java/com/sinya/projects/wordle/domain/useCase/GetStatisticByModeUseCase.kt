package com.sinya.projects.wordle.domain.useCase

import android.util.Log
import com.sinya.projects.wordle.data.local.database.entity.OfflineStatistic
import com.sinya.projects.wordle.domain.error.StatisticModeNotFoundException
import com.sinya.projects.wordle.domain.repository.StatisticRepository
import jakarta.inject.Inject

class GetStatisticByModeUseCase @Inject constructor(
    private val repository: StatisticRepository
) {
    suspend operator fun invoke(modeId: Int): Result<OfflineStatistic> {
        return try {
            val result = repository.getStatisticByMode(modeId) ?: return Result.failure(StatisticModeNotFoundException())
            Result.success(result)
        } catch (e: Exception) {
            Log.d("Check3", e.toString())
            Result.failure(e)
        }
    }
}