package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.domain.repository.DictionaryRepository
import com.sinya.projects.wordle.domain.model.DictionaryItem
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class GetAllWordsUseCase @Inject constructor(
    private val repository: DictionaryRepository
) {
    operator fun invoke(): Flow<List<DictionaryItem>> {
        return repository.getAllWords().flowOn(Dispatchers.IO)
    }
}