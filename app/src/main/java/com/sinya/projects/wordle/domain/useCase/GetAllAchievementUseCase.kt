package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.domain.repository.AchievementRepository
import com.sinya.projects.wordle.domain.model.AchieveItem
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class GetAllAchievementUseCase @Inject constructor(
    private val repository: AchievementRepository
) {
    operator fun invoke(lang: String): Flow<List<AchieveItem>> {
        return repository.observeAchievements(lang).flowOn(Dispatchers.IO)
    }
}