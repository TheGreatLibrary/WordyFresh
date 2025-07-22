package com.sinya.projects.wordle.data.supabase.sync

import android.content.Context
import android.util.Log
import com.sinya.projects.wordle.WordyApplication
import com.sinya.projects.wordle.data.supabase.Syncable
import com.sinya.projects.wordle.data.local.entity.OfflineStatistic
import com.sinya.projects.wordle.data.supabase.entity.Profiles
import com.sinya.projects.wordle.data.supabase.entity.SyncStatistic
import com.sinya.projects.wordle.data.supabase.mapper.toMap
import com.sinya.projects.wordle.utils.getCurrentTime
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

object StatisticSync : Syncable {
    override suspend fun toSupabase(context: Context, userId: String) {
        val db = WordyApplication.database
        val supabase = WordyApplication.supabaseClient

        try {
            val remote = supabase
                .from("sync_statistic")
                .select {
                    filter { eq("user_id", userId) }
                }
                .decodeList<SyncStatistic>()
            val offline = db.offlineStatisticDao().getAllStatistic()

            val merged = mergeStatistics(remote, offline, userId)

            val json = Json { encodeDefaults = true }

            val jsonString = merged.map {
                val jsonString = json.encodeToString(it)
                json.parseToJsonElement(jsonString).jsonObject
            }

            val updated =  supabase.from("sync_statistic").upsert(jsonString)

            if (updated != null) {
                db.offlineStatisticDao().clearAll()
            }
        } catch (e: Exception) {
            Log.e("StatisticSync", "Ошибка отправки: ${e.localizedMessage}")
        }
    }

    override suspend fun fromSupabase(context: Context, userId: String) {
        val db = WordyApplication.database

        try {
            val remote = WordyApplication.supabaseClient
                .from("sync_statistic")
                .select {
                    filter { eq("user_id", userId) }
                }
                .decodeList<SyncStatistic>()

            val local = db.syncStatisticDao().getAllStatistic()
            val localMap = local.associateBy { it.modeId }

            val filtered = remote.filter { remoteItem ->
                val localItem = localMap[remoteItem.modeId]
                localItem == null || remoteItem.updatedAt > localItem.updatedAt
            }

            db.syncStatisticDao().insertOrReplace(filtered)

        } catch (e: Exception) {
            Log.e("StatisticSync", "Ошибка загрузки: ${e.localizedMessage}")
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
                countGame = (remoteStat.countGame + local.countGame)?: 0,
                currentStreak = remoteStat.currentStreak + local.currentStreak + 0,
                bestStreak = maxOf(remoteStat.bestStreak, local.bestStreak, 0),
                winGame = remoteStat.winGame + local.winGame + 0,
                sumTime = (remoteStat.sumTime + local.sumTime)?: 0,
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
                    countGame = local.countGame?: 0,
                    currentStreak = local.currentStreak + 0,
                    bestStreak = local.bestStreak + 0,
                    winGame = local.winGame + 0,
                    sumTime = local.sumTime?: 0,
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