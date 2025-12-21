package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.data.local.database.entity.OfflineDictionary
import com.sinya.projects.wordle.domain.error.DefinitionNotFoundException
import com.sinya.projects.wordle.domain.error.NoInternetException
import com.sinya.projects.wordle.domain.error.WordNotFoundException
import com.sinya.projects.wordle.domain.repository.DictionaryRepository
import jakarta.inject.Inject

class InsertWordInDictionaryUseCase @Inject constructor(
    private val repository: DictionaryRepository
) {
    suspend operator fun invoke(word: String): Result<String> {
        return try {
            val wordObj = repository.getWord(word) ?: return Result.failure(WordNotFoundException())

            return repository.getDefinitionForWord(word).fold(
                onSuccess = { definition ->
                    repository.insertOrUpdateDescription(OfflineDictionary(wordObj.id, definition))
                    Result.success(definition)
                },
                onFailure = { exception ->
                    when (exception) {
                        is NoInternetException,
                        is DefinitionNotFoundException -> {
                            repository.insertOrUpdateDescription(OfflineDictionary(wordObj.id, exception.message ?: ""))
                            Result.success(exception.message ?: "")
                        }
                        else -> Result.failure(exception)
                    }
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}