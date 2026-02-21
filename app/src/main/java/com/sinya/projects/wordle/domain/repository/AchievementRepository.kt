package com.sinya.projects.wordle.domain.repository

import com.sinya.projects.wordle.data.local.database.dao.AchievementsDao
import com.sinya.projects.wordle.data.local.database.dao.OfflineAchievementsDao
import com.sinya.projects.wordle.data.local.database.dao.SyncAchievementsDao
import com.sinya.projects.wordle.data.local.database.entity.OfflineAchievements
import com.sinya.projects.wordle.domain.error.UserNotAuthenticatedException
import com.sinya.projects.wordle.domain.model.AchieveItem
import com.sinya.projects.wordle.domain.source.SupabaseAchievementDataSource
import com.sinya.projects.wordle.domain.source.SupabaseAuthDataSource
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

interface AchievementRepository {

    // AchievementScreen
    fun observeAchievements(): Flow<List<AchieveItem>>
    suspend fun clearAllAchievement(): Result<Unit>

    // AchieveManager
    suspend fun getAllAchievements(): Result<List<AchieveItem>>
    suspend fun resetCount(id: Int): Result<Unit>
    suspend fun unlockIncrement(id: Int): Result<Unit>

    // ProfileScreen
    suspend fun clearLocal(): Result<Unit>

    // SyncViewModel
    suspend fun syncFromSupabase(): Result<Unit>
    suspend fun syncFromLocal() : Result<Unit>
}

class AchievementRepositoryImpl @Inject constructor(
    private val achievementsDao: AchievementsDao,
    private val syncAchievementsDao: SyncAchievementsDao,
    private val offlineAchievementsDao: OfflineAchievementsDao,
    private val supabaseAuthDataSource: SupabaseAuthDataSource,
    private val supabaseAchievementDataSource: SupabaseAchievementDataSource
) : AchievementRepository {

    // AchievementScreen

    override suspend fun clearAllAchievement(): Result<Unit> {
        return try {
            offlineAchievementsDao.clearAll()

            val user = supabaseAuthDataSource.getCurrentUser()
            if (user != null) supabaseAchievementDataSource.clearAllAchievements(user.id)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeAchievements(): Flow<List<AchieveItem>> {
        return achievementsDao.observeAchievements().catch { emit(emptyList()) }
    }

    override suspend fun getAllAchievements(): Result<List<AchieveItem>> {
        return try {
            Result.success(achievementsDao.getAchievementsList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // AchievementManager (GameScreen)

    override suspend fun unlockIncrement(id: Int): Result<Unit> {
        return try {
            val updated = offlineAchievementsDao.increment(id)
            if (updated == 0) offlineAchievementsDao.insert(OfflineAchievements(achieveId = id, count = 1))

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resetCount(id: Int): Result<Unit> {
        return try {
            offlineAchievementsDao.resetCount(id)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ProfileScreen

    override suspend fun clearLocal(): Result<Unit> {
        return try {
            offlineAchievementsDao.clearAll()
            syncAchievementsDao.clearAll()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // SyncViewModel

    override suspend fun syncFromSupabase(): Result<Unit> {
        return try {
            val user = supabaseAuthDataSource.getCurrentUser()
                ?: return Result.failure(UserNotAuthenticatedException())

            supabaseAchievementDataSource.syncFromSupabase(user.id)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncFromLocal(): Result<Unit> {
        return try {
            val user = supabaseAuthDataSource.getCurrentUser()
                ?: return Result.failure(UserNotAuthenticatedException())

            supabaseAchievementDataSource.syncToSupabase(user.id)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

