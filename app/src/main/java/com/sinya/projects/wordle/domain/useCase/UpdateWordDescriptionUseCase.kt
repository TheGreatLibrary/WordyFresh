package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.data.local.database.entity.OfflineDictionary
import com.sinya.projects.wordle.domain.repository.DictionaryRepository
import com.sinya.projects.wordle.domain.model.DictionaryItem
import jakarta.inject.Inject

class UpdateWordDescriptionUseCase @Inject constructor(
    private val repository: DictionaryRepository
) {
    suspend operator fun invoke(item: DictionaryItem): Result<DictionaryItem> {
        return repository.getDefinitionForWord(item.word).fold(
            onSuccess = { definition ->
                val updatedItem = item.copy(description = definition)
                repository.insertOrUpdateDescription(OfflineDictionary(updatedItem.id, updatedItem.description))
                Result.success(updatedItem)
            },
            onFailure = { exception ->
                Result.failure(exception)
            }
        )
    }
}