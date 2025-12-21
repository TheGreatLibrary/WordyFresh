package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.domain.repository.DictionaryRepository
import com.sinya.projects.wordle.domain.model.DictionaryItem
import jakarta.inject.Inject

class GetAllWordsUseCase @Inject constructor(
    private val repository: DictionaryRepository
) {
    suspend operator fun invoke(): Result<List<DictionaryItem>> {
        return try {
            Result.success(repository.getAllWords())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}