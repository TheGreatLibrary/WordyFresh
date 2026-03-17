package com.sinya.projects.wordle.domain.repository

import com.sinya.projects.wordle.data.local.database.dao.OfflineStatisticDao
import com.sinya.projects.wordle.data.local.database.dao.SyncStatisticDao
import com.sinya.projects.wordle.data.local.database.entity.ModesStatistics
import com.sinya.projects.wordle.data.local.database.entity.OfflineStatistics
import com.sinya.projects.wordle.domain.enums.GameMode
import com.sinya.projects.wordle.domain.error.UserNotAuthenticatedException
import com.sinya.projects.wordle.domain.model.GameRow
import com.sinya.projects.wordle.domain.model.StatAggregated
import com.sinya.projects.wordle.domain.source.SupabaseAuthDataSource
import com.sinya.projects.wordle.domain.source.SupabaseStatisticsDataSource
import jakarta.inject.Inject

interface StatisticRepository {
    // StatisticScreen
    suspend fun getAggregatedAll(): List<StatAggregated>
    suspend fun clearAllStatistics(): Result<Unit>

    // GameScreen
    suspend fun getAllStatisticByMode(mode: Int) : Result<StatAggregated>
    suspend fun insertGame(stat: OfflineStatistics): Result<Unit>

    // ProfileScreen
    suspend fun clearLocal(): Result<Unit>

    // SyncViewModel
    suspend fun syncFromSupabase(): Result<Unit>
    suspend fun syncFromLocal(): Result<Unit>

    // AchievementManager
    suspend fun getCurrentStreak(isWin: Boolean, currentResult: Boolean): Int
}

class StatisticRepositoryImpl @Inject constructor(
    private val offlineStatisticDao: OfflineStatisticDao,
    private val syncStatisticDao: SyncStatisticDao,
    private val supabaseAuthDataSource: SupabaseAuthDataSource,
    private val supabaseStatisticsDataSource: SupabaseStatisticsDataSource
) : StatisticRepository {

    // StatisticScreen

    override suspend fun getAggregatedAll(): List<StatAggregated> {
        val modes = listOf(ModesStatistics(GameMode.ALL.id)) + offlineStatisticDao.getModes()
        val list = listOf(offlineStatisticDao.getAggregatedTotal(GameMode.ALL.id)) + offlineStatisticDao.getAggregatedAll()
        val streak = offlineStatisticDao.getAllGamesOrdered()
        return modes.map { item ->
            val streaks = calculateStreaks(if (item.id == GameMode.ALL.id) streak else streak.filter { it.modeId == item.id })
            (list.firstOrNull { it.modeId == item.id } ?: StatAggregated(item.id)).copy(
                currentStreak = streaks.first,
                bestStreak = streaks.second
            )
        }
    }

    private fun calculateStreaks(games: List<GameRow>): Pair<Int, Int> {
        var best = 0
        var temp = 0
        for (game in games) {
            if (game.result == 1) { temp++; best = maxOf(best, temp) }
            else temp = 0
        }
        var current = 0
        for (game in games.reversed()) {
            if (game.result == 1) current++
            else break
        }
        return current to best
    }

    override suspend fun clearAllStatistics(): Result<Unit> {
        return try {
            offlineStatisticDao.clearAll()

            val user = supabaseAuthDataSource.getCurrentUser()
            if (user != null) supabaseStatisticsDataSource.clearAllStatistics(user.id)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // GameScreen

    override suspend fun getAllStatisticByMode(mode: Int): Result<StatAggregated> {
        return try {
            val streak = offlineStatisticDao.getAllGamesOrdered()
            val streaks = calculateStreaks(streak.filter { it.modeId == mode })

            Result.success(offlineStatisticDao.getAggregatedByMode(mode).copy(
                currentStreak = streaks.first,
                bestStreak = streaks.second
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun insertGame(stat: OfflineStatistics): Result<Unit> {
        return try {
            offlineStatisticDao.insertGame(stat)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ProfileScreen

    override suspend fun clearLocal(): Result<Unit> {
        return try {
            offlineStatisticDao.clearAll()
            syncStatisticDao.clearAll()

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

    // AchievementManager

    override suspend fun getCurrentStreak(isWin: Boolean, currentResult: Boolean): Int {
        val games = offlineStatisticDao.getAllGamesOrdered()
        val allGames = games + GameRow(modeId = 0, result = if (currentResult) 1 else 0, createdAt = "")
        var streak = 0
        for (game in allGames.reversed()) {
            if ((game.result == 1) == isWin) streak++
            else break
        }
        return streak
    }
}