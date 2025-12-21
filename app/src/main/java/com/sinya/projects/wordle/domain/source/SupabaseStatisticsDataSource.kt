package com.sinya.projects.wordle.domain.source

import android.util.Log
import com.sinya.projects.wordle.data.local.database.dao.OfflineStatisticDao
import com.sinya.projects.wordle.data.local.database.dao.SyncStatisticDao
import com.sinya.projects.wordle.data.local.database.entity.OfflineStatistic
import com.sinya.projects.wordle.data.remote.supabase.entity.SyncStatistic
import com.sinya.projects.wordle.utils.getCurrentTime
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

interface SupabaseStatisticsDataSource {
    suspend fun fetchStatistics(userId: String): Result<List<SyncStatistic>>
    suspend fun upsertStatistics(statistics: List<SyncStatistic>): Result<Unit>
    suspend fun clearAllStatistics(userId: String): Result<Unit>
    suspend fun syncToSupabase(userId: String): Result<Unit>
    suspend fun syncFromSupabase(userId: String): Result<Unit>
}

class SupabaseStatisticsDataSourceImpl @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val offlineStatisticDao: OfflineStatisticDao,
    private val syncStatisticDao: SyncStatisticDao
) : SupabaseStatisticsDataSource {

    override suspend fun fetchStatistics(userId: String): Result<List<SyncStatistic>> {
        return withContext(Dispatchers.IO) {
            try {
                val statistics = supabaseClient
                    .from("sync_statistic")
                    .select { filter { eq("user_id", userId) } }
                    .decodeList<SyncStatistic>()

                Result.success(statistics)
            } catch (e: Exception) {
                Log.e("SupabaseStatisticsDataSource", "Error fetching statistics", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun upsertStatistics(statistics: List<SyncStatistic>): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val json = Json { encodeDefaults = true }

                val jsonString = statistics.map {
                    val encoded = json.encodeToString(it)
                    val obj = json.parseToJsonElement(encoded).jsonObject
                    Log.d("SUPABASE_JSON_OBJECT", obj.toString())
                    obj
                }

                supabaseClient.from("sync_statistic").upsert(jsonString) {
                    onConflict = "user_id,mode_id"
                }
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
                supabaseClient.from("sync_statistic")
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
                val remote = fetchStatistics(userId).getOrThrow()
                val offline = offlineStatisticDao.getAllStatistic()

                val merged = mergeStatistics(remote, offline, userId)

                upsertStatistics(merged)
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
                val remote = fetchStatistics(userId).getOrThrow()
                val local = syncStatisticDao.getAllStatistic()
                val localMap = local.associateBy { it.modeId }

                val filtered = remote.filter { remoteItem ->
                    val localItem = localMap[remoteItem.modeId]
                    localItem == null || remoteItem.updatedAt > localItem.updatedAt
                }

                if (filtered.isNotEmpty()) {
                    syncStatisticDao.insertOrReplace(filtered)
                }

                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("SupabaseStatisticsDataSource", "Error syncing from Supabase", e)
                Result.failure(e)
            }
        }
    }

    private fun mergeStatistics(
        remote: List<SyncStatistic>,
        offline: List<OfflineStatistic>,
        userId: String
    ): List<SyncStatistic> {
        val updatedAt = getCurrentTime()
        val remoteMap = remote.associateBy { it.modeId }.toMutableMap()

        for (local in offline) {
            val remoteStat = remoteMap[local.modeId]

            val merged = remoteStat?.copy(
                userId = userId,
                countGame = (remoteStat.countGame + local.countGame) ?: 0,
                currentStreak = remoteStat.currentStreak + local.currentStreak + 0,
                bestStreak = maxOf(remoteStat.bestStreak, local.bestStreak, 0),
                winGame = remoteStat.winGame + local.winGame + 0,
                sumTime = (remoteStat.sumTime + local.sumTime) ?: 0,
                firstTry = remoteStat.firstTry + local.firstTry + 0,
                secondTry = remoteStat.secondTry + local.secondTry + 0,
                thirdTry = remoteStat.thirdTry + local.thirdTry + 0,
                fourthTry = remoteStat.fourthTry + local.fourthTry + 0,
                fifthTry = remoteStat.fifthTry + local.fifthTry + 0,
                sixthTry = remoteStat.sixthTry + local.sixthTry + 0,
                updatedAt = updatedAt
            )
                ?: SyncStatistic(
                    userId = userId,
                    modeId = local.modeId + 0,
                    countGame = local.countGame ?: 0,
                    currentStreak = local.currentStreak + 0,
                    bestStreak = local.bestStreak + 0,
                    winGame = local.winGame + 0,
                    sumTime = local.sumTime ?: 0,
                    firstTry = local.firstTry + 0,
                    secondTry = local.secondTry + 0,
                    thirdTry = local.thirdTry + 0,
                    fourthTry = local.fourthTry + 0,
                    fifthTry = local.fifthTry + 0,
                    sixthTry = local.sixthTry + 0,
                    updatedAt = updatedAt
                )

            remoteMap[local.modeId] = merged
        }

        return remoteMap.values.toList()
    }
}