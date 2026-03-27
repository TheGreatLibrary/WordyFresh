package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.data.local.database.entity.ModeStatisticsTranslations
import com.sinya.projects.wordle.domain.repository.StatisticRepository
import jakarta.inject.Inject

class GetModesUseCase @Inject constructor(
    private val statisticRepository: StatisticRepository
) {
    suspend operator fun invoke(lang: String): Result<List<ModeStatisticsTranslations>> {
        return statisticRepository.getModes(lang)
    }
}