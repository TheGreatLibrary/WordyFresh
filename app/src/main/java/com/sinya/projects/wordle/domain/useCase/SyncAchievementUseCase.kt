package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.data.remote.supabase.SessionManager
import com.sinya.projects.wordle.domain.error.UserNotAuthenticatedException
import com.sinya.projects.wordle.domain.repository.AchievementRepository
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncAchievementUseCase @Inject constructor(
    private val sessionManager: SessionManager,
    private val repository: AchievementRepository
) {
    suspend operator fun invoke(): Result<Unit> = withContext(Dispatchers.IO) {
        val userId = sessionManager.currentUserId
            ?: return@withContext Result.failure(UserNotAuthenticatedException())

        repository.syncFromSupabase(userId)
    }
}