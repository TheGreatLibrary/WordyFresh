package com.sinya.projects.wordle.domain.useCase

import android.util.Log
import com.sinya.projects.wordle.domain.error.UserNotAuthenticatedException
import com.sinya.projects.wordle.domain.repository.AchievementRepository
import com.sinya.projects.wordle.domain.repository.DictionaryRepository
import com.sinya.projects.wordle.domain.repository.ProfileRepository
import com.sinya.projects.wordle.domain.repository.StatisticRepository
import com.sinya.projects.wordle.domain.source.SupabaseAuthDataSource
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncDataUseCase @Inject constructor(
    private val authRepository: SupabaseAuthDataSource,
    private val statisticsRepository: StatisticRepository,
    private val dictionaryRepository: DictionaryRepository,
    private val achievementsRepository: AchievementRepository,
    private val profileRepository: ProfileRepository,
) {
    suspend operator fun invoke(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userId = authRepository.getCurrentUser()
                ?: return@withContext Result.failure(UserNotAuthenticatedException())

            Log.d("SyncUseCase", "Синхронизация для пользователя: $userId")

            profileRepository.syncFromLocal().getOrThrow()
            achievementsRepository.syncFromLocal().getOrThrow()
            statisticsRepository.syncFromLocal().getOrThrow()
            dictionaryRepository.syncFromLocal().getOrThrow()

            Log.d("SupabaseSync", "Данные успешно отправлены")

            profileRepository.syncFromSupabase().getOrThrow()
            achievementsRepository.syncFromSupabase().getOrThrow()
            statisticsRepository.syncFromSupabase().getOrThrow()
            dictionaryRepository.syncFromSupabase().getOrThrow()

            Log.d("SyncUseCase", "✅ Синхронизация завершена")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SyncUseCase", "❌ Ошибка синхронизации: ${e.message}")
            Result.failure(e)
        }
    }
}