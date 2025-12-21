package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.domain.repository.DictionaryRepository
import jakarta.inject.Inject

class ClearAllDictionaryUseCase @Inject constructor(
    private val repository: DictionaryRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            repository.clearAllDictionary()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}