package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.domain.error.WordNotFoundException
import com.sinya.projects.wordle.domain.repository.DictionaryRepository
import jakarta.inject.Inject

class WordExistsUseCase @Inject constructor(
    private val dictionaryRepository: DictionaryRepository
) {
    suspend operator fun invoke(
        word: String,
        lang: String,
        length: Int,
        ratingStatus: Int
    ): Result<Unit> {
        return try {
            val result = dictionaryRepository.existWord(
                word = word,
                lang = lang,
                length = length,
                ratingStatus = ratingStatus
            )

            if (!result) Result.failure(WordNotFoundException())
            else Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}