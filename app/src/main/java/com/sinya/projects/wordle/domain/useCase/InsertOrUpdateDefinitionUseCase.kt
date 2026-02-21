package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.domain.repository.DictionaryRepository
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InsertOrUpdateDefinitionUseCase @Inject constructor(
    private val repository: DictionaryRepository
) {
    suspend operator fun invoke(word: String): Result<String> = withContext(Dispatchers.IO) {
        repository.fetchAndSaveDefinition(word)
    }
}