package com.sinya.projects.wordle.domain.source

import android.util.Log
import com.sinya.projects.wordle.data.local.database.dao.OfflineAchievementsDao
import com.sinya.projects.wordle.data.local.database.dao.SyncAchievementsDao
import com.sinya.projects.wordle.data.remote.supabase.entity.SyncAchievements
import com.sinya.projects.wordle.domain.checker.NetworkChecker
import com.sinya.projects.wordle.domain.error.NoInternetException
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

interface SupabaseAchievementDataSource {
    suspend fun fetchAchievements(userId: String): Result<List<SyncAchievements>>
    suspend fun clearAllAchievements(userId: String): Result<Unit>
    suspend fun syncToSupabase(userId: String): Result<Unit>
    suspend fun syncFromSupabase(userId: String): Result<Unit>
}

class SupabaseAchievementDataSourceImpl @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val offlineAchievementsDao: OfflineAchievementsDao,
    private val syncAchievementsDao: SyncAchievementsDao,
    private val networkChecker: NetworkChecker
) : SupabaseAchievementDataSource {

    override suspend fun fetchAchievements(userId: String): Result<List<SyncAchievements>> {
        return withContext(Dispatchers.IO) {
            try {
                if (!networkChecker.isInternetAvailable()) return@withContext Result.failure(NoInternetException())

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

    override suspend fun clearAllAchievements(userId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                if (!networkChecker.isInternetAvailable()) return@withContext Result.failure(NoInternetException())

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
                if (!networkChecker.isInternetAvailable()) return@withContext Result.failure(NoInternetException())

                val offline = offlineAchievementsDao.getAchievements()

                if (offline.isNotEmpty()) {
                    offline.forEach { item ->
                        Log.d("Achieve", item.toString())
                        supabaseClient.postgrest.rpc(
                            "increment_achievement",
                            buildJsonObject {
                                put("p_user_id", userId)
                                put("p_achieve_id", item.achieveId)
                                put("p_count", item.count)
                            }
                        )
                    }
                }

                offlineAchievementsDao.clearAll()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun syncFromSupabase(userId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                if (!networkChecker.isInternetAvailable()) return@withContext Result.failure(NoInternetException())

                val remote = fetchAchievements(userId).getOrThrow()

                syncAchievementsDao.replaceAll(remote)
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("SupabaseDictionaryDataSource", "Error syncing from Supabase", e)
                Result.failure(e)
            }
        }
    }
}