package com.sinya.projects.wordle.domain.source

import android.util.Log
import com.sinya.projects.wordle.data.local.database.dao.OfflineAchievementsDao
import com.sinya.projects.wordle.data.local.database.dao.SyncAchievementsDao
import com.sinya.projects.wordle.data.local.database.entity.OfflineAchievements
import com.sinya.projects.wordle.data.remote.supabase.entity.SyncAchievements
import com.sinya.projects.wordle.utils.getCurrentTime
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface SupabaseAchievementDataSource {
    suspend fun fetchAchievements(userId: String): Result<List<SyncAchievements>>
    suspend fun upsertAchievements(achievements: List<SyncAchievements>): Result<Unit>
    suspend fun clearAllAchievements(userId: String): Result<Unit>
    suspend fun syncToSupabase(userId: String): Result<Unit>
    suspend fun syncFromSupabase(userId: String): Result<Unit>
}

class SupabaseAchievementDataSourceImpl @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val offlineAchievementsDao: OfflineAchievementsDao,
    private val syncAchievementsDao: SyncAchievementsDao
) : SupabaseAchievementDataSource {

    override suspend fun fetchAchievements(userId: String): Result<List<SyncAchievements>> {
        return withContext(Dispatchers.IO) {
            try {
                val achievements = supabaseClient
                    .from("sync_achievements")
                    .select { filter { eq("user_id", userId) } }
                    .decodeList<SyncAchievements>()

                Result.success(achievements)
            } catch (e: Exception) {
                Log.e("SupabaseAchievementsDataSource", "Error fetching achievements", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun upsertAchievements(achievements: List<SyncAchievements>): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                supabaseClient.from("sync_achievements").upsert(achievements)
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("SupabaseAchievementsDataSource", "Error upserting achievements", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun clearAllAchievements(userId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                supabaseClient.from("sync_achievements")
                    .delete { filter { eq("user_id", userId) } }
                syncFromSupabase(userId).getOrThrow()
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("SupabaseAchievementsDataSource", "Error clearing achievements", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun syncToSupabase(userId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val remote = fetchAchievements(userId).getOrThrow()
                val offline = offlineAchievementsDao.getAchievements()
                val merged = mergeAchievements(remote, offline, userId)

                upsertAchievements(merged)

                offlineAchievementsDao.clearAll()

                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("SupabaseAchievementsDataSource", "Error syncing to Supabase", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun syncFromSupabase(userId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val remote = fetchAchievements(userId).getOrThrow()
                val local = syncAchievementsDao.getAll()
                val localMap = local.associateBy { it.achieveId }

                val filtered = remote.filter { remoteItem ->
                    val localItem = localMap[remoteItem.achieveId]
                    localItem == null || remoteItem.updatedAt > localItem.updatedAt
                }

                syncAchievementsDao.insertOrReplace(filtered)
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("SupabaseDictionaryDataSource", "Error syncing from Supabase", e)
                Result.failure(e)
            }
        }
    }

    private fun mergeAchievements(
        remote: List<SyncAchievements>,
        offline: List<OfflineAchievements>,
        userId: String
    ): List<SyncAchievements> {
        val updatedAt = getCurrentTime()
        val remoteMap = remote.associateBy { it.achieveId }.toMutableMap()

        for (local in offline) {
            val remoteItem = remoteMap[local.achieveId]

            val merged = remoteItem?.copy(
                userId = userId,
                count = remoteItem.count + local.count,
                updatedAt = updatedAt
            ) ?: SyncAchievements(
                userId = userId,
                achieveId = local.achieveId,
                count = local.count,
                updatedAt = updatedAt
            )

            remoteMap[local.achieveId] = merged
        }

        return remoteMap.values.toList()
    }
}