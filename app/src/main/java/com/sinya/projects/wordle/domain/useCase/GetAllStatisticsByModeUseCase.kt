package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.domain.model.StatAggregated
import com.sinya.projects.wordle.domain.repository.StatisticRepository
import jakarta.inject.Inject

class GetAllStatisticsByModeUseCase @Inject constructor(
    private val repository: StatisticRepository
) {
    suspend operator fun invoke(mode: Int): Result<StatAggregated> {
        return repository.getAllStatisticByMode(mode)
    }
}