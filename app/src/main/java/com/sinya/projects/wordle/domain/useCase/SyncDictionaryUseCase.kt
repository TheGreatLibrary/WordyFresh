package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.data.remote.supabase.SessionManager
import com.sinya.projects.wordle.domain.error.UserNotAuthenticatedException
import com.sinya.projects.wordle.domain.repository.DictionaryRepository
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncDictionaryUseCase @Inject constructor(
    private val sessionManager: SessionManager,
    private val repository: DictionaryRepository
) {
    suspend operator fun invoke(): Result<Unit> = withContext(Dispatchers.IO) {
        val userId = sessionManager.currentUserId
            ?: return@withContext Result.failure(UserNotAuthenticatedException())

        repository.syncFromSupabase(userId)
    }
}