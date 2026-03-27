package com.sinya.projects.wordle.domain.source

import android.util.Log
import com.sinya.projects.wordle.data.local.database.dao.OfflineStatisticDao
import com.sinya.projects.wordle.data.local.database.dao.SyncStatisticDao
import com.sinya.projects.wordle.data.remote.supabase.entity.SyncStatistics
import com.sinya.projects.wordle.data.remote.supabase.mapper.toSyncList
import com.sinya.projects.wordle.domain.checker.NetworkChecker
import com.sinya.projects.wordle.domain.error.NoInternetException
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

interface SupabaseStatisticsDataSource {
    suspend fun fetchStatistics(userId: String): Result<List<SyncStatistics>>
    suspend fun upsertStatistics(statistics: List<SyncStatistics>): Result<Unit>
    suspend fun clearAllStatistics(userId: String): Result<Unit>
    suspend fun syncToSupabase(userId: String): Result<Unit>
    suspend fun syncFromSupabase(userId: String): Result<Unit>
}

class SupabaseStatisticsDataSourceImpl @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val offlineStatisticDao: OfflineStatisticDao,
    private val syncStatisticDao: SyncStatisticDao,
    private val networkChecker: NetworkChecker
) : SupabaseStatisticsDataSource {

    override suspend fun fetchStatistics(userId: String): Result<List<SyncStatistics>> {
        return withContext(Dispatchers.IO) {
            try {
                if (!networkChecker.isInternetAvailable()) return@withContext Result.failure(NoInternetException())

                val statistics = supabaseClient
                    .from("sync_statistics")
                    .select { filter { eq("user_id", userId) } }
                    .decodeList<SyncStatistics>()

                Result.success(statistics)
            } catch (e: Exception) {
                Log.e("SupabaseStatisticsDataSource", "Error fetching statistics", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun upsertStatistics(statistics: List<SyncStatistics>): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                if (!networkChecker.isInternetAvailable()) return@withContext Result.failure(NoInternetException())

                val json = Json { encodeDefaults = true }

                val jsonString = statistics.map {
                    json.parseToJsonElement(json.encodeToString(it)).jsonObject
                }

                supabaseClient.from("sync_statistics").upsert(jsonString)
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("SupabaseStatisticsDataSource", "Error upserting statistics", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun clearAllStatistics(userId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                if (!networkChecker.isInternetAvailable()) return@withContext Result.failure(NoInternetException())

                supabaseClient.from("sync_statistics")
                    .delete { filter { eq("user_id", userId) } }

                syncFromSupabase(userId).getOrThrow()
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("SupabaseStatisticsDataSource", "Error clearing statistics", e)
                Result.failure(e)
            }
        }

    }

    override suspend fun syncToSupabase(userId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                if (!networkChecker.isInternetAvailable()) return@withContext Result.failure(NoInternetException())

                val offline = offlineStatisticDao
                    .getAllStatistic()
                    .toSyncList(userId)

                if (offline.isNotEmpty()) {
                    upsertStatistics(offline).getOrThrow()
                }

                offlineStatisticDao.clearAll()

                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("SupabaseStatisticsDataSource", "Error syncing to Supabase", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun syncFromSupabase(userId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                if (!networkChecker.isInternetAvailable()) return@withContext Result.failure(NoInternetException())

                val remote = fetchStatistics(userId).getOrThrow()
                syncStatisticDao.clearAll()

                if (remote.isNotEmpty()) {
                    syncStatisticDao.insertStatistic(remote)
                }

                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("SupabaseStatisticsDataSource", "Error syncing from Supabase", e)
                Result.failure(e)
            }
        }
    }
}