package com.sinya.projects.wordle.domain.repository

import android.util.Log
import com.sinya.projects.wordle.data.local.database.dao.AchievementsDao
import com.sinya.projects.wordle.data.local.database.dao.OfflineAchievementsDao
import com.sinya.projects.wordle.data.local.database.dao.SyncAchievementsDao
import com.sinya.projects.wordle.data.local.database.entity.OfflineAchievements
import com.sinya.projects.wordle.domain.error.UserNotAuthenticatedException
import com.sinya.projects.wordle.domain.model.AchieveItem
import com.sinya.projects.wordle.domain.source.SupabaseAchievementDataSource
import com.sinya.projects.wordle.domain.source.SupabaseAuthDataSource
import jakarta.inject.Inject

interface AchievementRepository {
    suspend fun getAllAchievements(): List<AchieveItem>
    suspend fun clearAllAchievement()
    suspend fun clearLocal()
    suspend fun unlockIncrement(id: Int)
    suspend fun syncFromSupabase(): Result<Unit>
    suspend fun syncFromLocal() : Result<Unit>
    suspend fun resetCount(id: Int)
}

class AchievementRepositoryImpl @Inject constructor(
    private val achievementsDao: AchievementsDao,
    private val syncAchievementsDao: SyncAchievementsDao,
    private val offlineAchievementsDao: OfflineAchievementsDao,
    private val supabaseAuthDataSource: SupabaseAuthDataSource,
    private val supabaseAchievementDataSource: SupabaseAchievementDataSource
) : AchievementRepository {

    override suspend fun clearAllAchievement() {
        offlineAchievementsDao.clearAll()

        val user = supabaseAuthDataSource.getCurrentUser()
        if (user != null) {
            supabaseAchievementDataSource.clearAllAchievements(user.id)
        } else {
            Log.d("AchievementRepository", "User not authenticated, skipping remote clear")
        }
    }

    override suspend fun clearLocal() {
        offlineAchievementsDao.clearAll()
        syncAchievementsDao.clearAll()
    }

    override suspend fun getAllAchievements(): List<AchieveItem> {
        val list = achievementsDao.getAchievementsList()
        Log.d("ACCCC", list.toString())
        return list
    }

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

    override suspend fun unlockIncrement(id: Int) {
        val updated = offlineAchievementsDao.increment(id)
        if (updated == 0) offlineAchievementsDao.insert(OfflineAchievements(achieveId = id, count = 1))
    }

    override suspend fun resetCount(id: Int) {
        offlineAchievementsDao.resetCount(id)
    }
}

