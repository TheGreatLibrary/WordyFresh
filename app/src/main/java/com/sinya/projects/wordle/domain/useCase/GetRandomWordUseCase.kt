package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.domain.repository.DictionaryRepository
import jakarta.inject.Inject

class GetRandomWordUseCase @Inject constructor(
    private val dictionaryRepository: DictionaryRepository
) {
    suspend operator fun invoke(length: Int, lang: String, ratingStatus: Boolean): Result<String> {
        return dictionaryRepository.getRandomWord(length, lang, ratingStatus)
    }
}