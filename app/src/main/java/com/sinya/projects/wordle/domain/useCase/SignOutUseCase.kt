package com.sinya.projects.wordle.domain.useCase

import android.util.Log
import com.sinya.projects.wordle.data.remote.supabase.SessionManager
import com.sinya.projects.wordle.domain.checker.NetworkChecker
import com.sinya.projects.wordle.domain.error.NoInternetException
import com.sinya.projects.wordle.domain.error.UserNotAuthenticatedException
import com.sinya.projects.wordle.domain.repository.AchievementRepository
import com.sinya.projects.wordle.domain.repository.AvatarRepository
import com.sinya.projects.wordle.domain.repository.DictionaryRepository
import com.sinya.projects.wordle.domain.repository.ProfileRepository
import com.sinya.projects.wordle.domain.repository.StatisticRepository
import com.sinya.projects.wordle.domain.source.SupabaseAuthDataSource
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SignOutUseCase @Inject constructor(
    private val sessionManager: SessionManager,
    private val authRepository: SupabaseAuthDataSource,
    private val statisticsRepository: StatisticRepository,
    private val dictionaryRepository: DictionaryRepository,
    private val achievementsRepository: AchievementRepository,
    private val profileRepository: ProfileRepository,
    private val avatarRepository: AvatarRepository,
    private val networkChecker: NetworkChecker
) {
    suspend operator fun invoke(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (!networkChecker.isInternetAvailable()) return@withContext Result.failure(NoInternetException())

            val userId = sessionManager.currentUserId
                ?: return@withContext Result.success(Unit)

            statisticsRepository.syncFromLocal(userId).getOrThrow()
            dictionaryRepository.syncFromLocal(userId).getOrThrow()
            achievementsRepository.syncFromLocal(userId).getOrThrow()

            avatarRepository.deleteLocalAvatar(userId)

            statisticsRepository.clearLocal()
            dictionaryRepository.clearLocal()
            achievementsRepository.clearLocal()
            profileRepository.clearProfile()

            authRepository.signOut()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SignOutUseCase", "Error during sign out", e)
            Result.failure(e)
        }
    }
}