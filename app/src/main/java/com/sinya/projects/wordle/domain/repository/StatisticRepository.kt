package com.sinya.projects.wordle.domain.repository

import android.util.Log
import com.sinya.projects.wordle.data.local.database.dao.OfflineStatisticDao
import com.sinya.projects.wordle.data.local.database.dao.SyncStatisticDao
import com.sinya.projects.wordle.data.local.database.entity.OfflineStatistic
import com.sinya.projects.wordle.data.remote.supabase.entity.SyncStatistic
import com.sinya.projects.wordle.domain.enums.GameMode
import com.sinya.projects.wordle.domain.error.UserNotAuthenticatedException
import com.sinya.projects.wordle.domain.source.SupabaseAuthDataSource
import com.sinya.projects.wordle.domain.source.SupabaseStatisticsDataSource
import jakarta.inject.Inject

interface StatisticRepository {
    suspend fun getAllStatistic(): List<OfflineStatistic>
    suspend fun getMergedSummary(): OfflineStatistic
    fun getTotalStatistic(list: List<OfflineStatistic>, mode: GameMode): OfflineStatistic
    suspend fun getStatisticByMode(mode: Int): OfflineStatistic?
    suspend fun getAllStatisticByMode(mode: Int) : OfflineStatistic
    suspend fun clearAllStatistics()
    suspend fun clearLocal()
    suspend fun syncFromSupabase(): Result<Unit>
    suspend fun syncFromLocal(): Result<Unit>

    suspend fun updateStatistic(updated: OfflineStatistic)

}

class StatisticRepositoryImpl @Inject constructor(
    private val offlineStatisticDao: OfflineStatisticDao,
    private val syncStatisticDao: SyncStatisticDao,
    private val supabaseAuthDataSource: SupabaseAuthDataSource,
    private val supabaseStatisticsDataSource: SupabaseStatisticsDataSource
) : StatisticRepository {

    override suspend fun clearLocal() {
        offlineStatisticDao.clearAll()
        syncStatisticDao.clearAll()
    }

    override suspend fun getAllStatistic(): List<OfflineStatistic> {
        var offline = offlineStatisticDao.getAllStatistic()
        val sync = syncStatisticDao.getAllStatistic()

        if (offline.isEmpty()) {
            createBaseData()
            offline = offlineStatisticDao.getAllStatistic()
        }
        return mergeStatistics(offline, sync)
    }

    override suspend fun getMergedSummary(): OfflineStatistic {
        return offlineStatisticDao.getMergedSummary()
    }

    override fun getTotalStatistic(
        list: List<OfflineStatistic>,
        mode: GameMode
    ): OfflineStatistic {
        return list.firstOrNull { it.modeId == mode.id } ?: list.reduce { acc, stat ->
            acc.copy(
                countGame = acc.countGame + stat.countGame,
                currentStreak = acc.currentStreak + stat.currentStreak,
                bestStreak = maxOf(acc.bestStreak, stat.bestStreak),
                winGame = acc.winGame + stat.winGame,
                sumTime = acc.sumTime + stat.sumTime,
                firstTry = acc.firstTry + stat.firstTry,
                secondTry = acc.secondTry + stat.secondTry,
                thirdTry = acc.thirdTry + stat.thirdTry,
                fourthTry = acc.fourthTry + stat.fourthTry,
                fifthTry = acc.fifthTry + stat.fifthTry,
                sixthTry = acc.sixthTry + stat.sixthTry
            )
        }.copy(modeId = GameMode.ALL.id)
    }


    override suspend fun getStatisticByMode(mode: Int): OfflineStatistic? {
        var offline = offlineStatisticDao.getStatisticByMode(mode)

        Log.d("Check", offline.toString())
        if (offline == null) {
            createBaseData()
            offline = offlineStatisticDao.getStatisticByMode(mode)
            Log.d("Check2", offline.toString())
        }
        return offline
    }

    override suspend fun getAllStatisticByMode(mode: Int): OfflineStatistic {
        val offline = getStatisticByMode(mode) as OfflineStatistic
        val online = syncStatisticDao.getStatisticByMode(mode)

        val merge = mergeStatistic(offline, online)
        Log.d("Wor", merge.toString())
        return merge
    }


    override suspend fun clearAllStatistics() {
        offlineStatisticDao.clearAll()

        val user = supabaseAuthDataSource.getCurrentUser()
        if (user != null) {
            supabaseStatisticsDataSource.clearAllStatistics(user.id)
        } else {
            Log.d("StatisticRepository", "User not authenticated, skipping remote clear")
        }
    }

    override suspend fun syncFromSupabase(): Result<Unit> {
        return try {
            val user = supabaseAuthDataSource.getCurrentUser()
                ?: return Result.failure(UserNotAuthenticatedException())

            supabaseStatisticsDataSource.syncFromSupabase(user.id)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncFromLocal(): Result<Unit> {
        return try {
            val user = supabaseAuthDataSource.getCurrentUser()
                ?: return Result.failure(UserNotAuthenticatedException())

            supabaseStatisticsDataSource.syncToSupabase(user.id)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateStatistic(updated: OfflineStatistic) {
        offlineStatisticDao.updateStatistic(updated)
    }

    private suspend fun createBaseData() {
        val supportedModes = GameMode.getStatsOfDataBase()
            .map {
                OfflineStatistic(modeId = it.id)
            }

        offlineStatisticDao.insertStatisticList(supportedModes)
    }

    private fun mergeStatistics(
        offlineStats: List<OfflineStatistic>,
        syncStats: List<SyncStatistic>
    ): List<OfflineStatistic> {
        val syncMap = syncStats.associateBy { it.modeId }
        return offlineStats.map { offline ->
            val sync = syncMap[offline.modeId]
            if (sync != null) {
                mergeStatistic(offline, sync)
            } else {
                offline
            }
        }
    }

    private fun mergeStatistic(
        offline: OfflineStatistic,
        sync: SyncStatistic
    ): OfflineStatistic {
        return offline.copy(
            countGame = offline.countGame + sync.countGame,
            bestStreak = maxOf(offline.bestStreak, sync.bestStreak),
            winGame = offline.winGame + sync.winGame,
            sumTime = offline.sumTime + sync.sumTime,
            firstTry = offline.firstTry + sync.firstTry,
            secondTry = offline.secondTry + sync.secondTry,
            thirdTry = offline.thirdTry + sync.thirdTry,
            fourthTry = offline.fourthTry + sync.fourthTry,
            fifthTry = offline.fifthTry + sync.fifthTry,
            sixthTry = offline.sixthTry + sync.sixthTry,
        )
    }
}