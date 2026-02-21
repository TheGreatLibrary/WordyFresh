package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.data.local.database.entity.Words
import com.sinya.projects.wordle.domain.repository.DictionaryRepository
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetDataWordUseCase @Inject constructor(
    private val dictionaryRepository: DictionaryRepository
) {
    suspend operator fun invoke(word: String): Result<Words> = withContext(Dispatchers.IO) {
        dictionaryRepository.getWord(word)
    }
}