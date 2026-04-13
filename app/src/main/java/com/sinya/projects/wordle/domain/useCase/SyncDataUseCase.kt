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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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

            val pushTasks = listOf(
                async { profileRepository.syncFromLocal() },
                async { achievementsRepository.syncFromLocal() },
                async { statisticsRepository.syncFromLocal() },
                async { dictionaryRepository.syncFromLocal() }
            )
            pushTasks.awaitAll() // Ждем завершения всех отправок
            Log.d("SyncUseCase", "Всё локальное улетело в Supabase")

            // 2. Теперь забираем свежее (Pull)
            val pullTasks = listOf(
                async { profileRepository.syncFromSupabase() },
                async { achievementsRepository.syncFromSupabase() },
                async { statisticsRepository.syncFromSupabase() },
                async { dictionaryRepository.syncFromSupabase() }
            )
            pullTasks.awaitAll()

            Log.d("SyncUseCase", "✅ Синхронизация завершена")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SyncUseCase", "❌ Ошибка: ${e.message}")
            Result.failure(e)
        }
    }
}