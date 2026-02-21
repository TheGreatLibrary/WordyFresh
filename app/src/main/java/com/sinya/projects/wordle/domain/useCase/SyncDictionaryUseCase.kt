package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.domain.repository.DictionaryRepository
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncDictionaryUseCase @Inject constructor(
    private val repository: DictionaryRepository
) {
    suspend operator fun invoke(): Result<Unit> = withContext(Dispatchers.IO) {
        repository.syncFromSupabase()
    }
}