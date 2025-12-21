package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.data.local.database.entity.Words
import com.sinya.projects.wordle.domain.error.WordNotFoundException
import com.sinya.projects.wordle.domain.repository.DictionaryRepository
import jakarta.inject.Inject

class GetDataWordUseCase @Inject constructor(
    private val dictionaryRepository: DictionaryRepository
) {
    suspend operator fun invoke(word: String): Result<Words> {
        return try {
            val wordObj = dictionaryRepository.getWord(word)
                ?: return Result.failure(WordNotFoundException())

            Result.success(wordObj)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}