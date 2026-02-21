package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.domain.repository.DictionaryRepository
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WordExistsUseCase @Inject constructor(
    private val dictionaryRepository: DictionaryRepository
) {
    suspend operator fun invoke(
        word: String, lang: String, length: Int, ratingStatus: Int
    ): Result<Unit> = withContext(Dispatchers.IO) {
        dictionaryRepository.existWord(word = word, lang = lang, length = length, ratingStatus = ratingStatus)
    }
}