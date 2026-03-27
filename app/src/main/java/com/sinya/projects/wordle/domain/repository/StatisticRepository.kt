package com.sinya.projects.wordle.domain.repository

import android.util.Log
import com.sinya.projects.wordle.data.local.database.dao.OfflineStatisticDao
import com.sinya.projects.wordle.data.local.database.dao.SyncStatisticDao
import com.sinya.projects.wordle.data.local.database.entity.ModeStatisticsTranslations
import com.sinya.projects.wordle.data.local.database.entity.ModesStatistics
import com.sinya.projects.wordle.data.local.database.entity.OfflineStatistics
import com.sinya.projects.wordle.domain.enums.GameMode
import com.sinya.projects.wordle.domain.error.UserNotAuthenticatedException
import com.sinya.projects.wordle.domain.model.AttemptData
import com.sinya.projects.wordle.domain.model.GameRow
import com.sinya.projects.wordle.domain.model.StatAggregated
import com.sinya.projects.wordle.domain.model.StatAggregatedEntity
import com.sinya.projects.wordle.domain.source.SupabaseAuthDataSource
import com.sinya.projects.wordle.domain.source.SupabaseStatisticsDataSource
import com.sinya.projects.wordle.utils.calculateStreaks
import jakarta.inject.Inject

interface StatisticRepository {
    // StatisticScreen
    suspend fun getAggregatedAll(): Result<List<StatAggregated>>
    suspend fun clearAllStatistics(): Result<Unit>
    suspend fun getModes(lang: String): Result<List<ModeStatisticsTranslations>>

    // GameScreen
    suspend fun getAllStatisticByMode(mode: Int): Result<StatAggregated>
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

    override suspend fun getAggregatedAll(): Result<List<StatAggregated>> {
        return try {
            val modes = listOf(ModesStatistics(GameMode.ALL.id)) + offlineStatisticDao.getModes()
            val list = listOf(offlineStatisticDao.getAggregatedTotal(GameMode.ALL.id)) + offlineStatisticDao.getAggregatedAll()
            val streak = offlineStatisticDao.getAllGamesOrdered()

            val result = modes.map { item ->
                val streaks = calculateStreaks(
                    if (item.id == GameMode.ALL.id) streak
                    else streak.filter { it.modeId == item.id }
                )

                val base = list.firstOrNull { it.modeId == item.id } ?: StatAggregatedEntity(item.id)

                val attempts =
                    if (item.id == GameMode.ALL.id) offlineStatisticDao.getTotalAttemptBreakdown()
                    else offlineStatisticDao.getAttemptBreakdown(item.id)
                val langs =
                    if (item.id == GameMode.ALL.id) offlineStatisticDao.getTotalLangBreakdown()
                    else offlineStatisticDao.getLangBreakdown(item.id)
                val lengths =
                    if (item.id == GameMode.ALL.id) offlineStatisticDao.getTotalLengthBreakdown()
                    else offlineStatisticDao.getLengthBreakdown(item.id)

                StatAggregated(
                    modeId = base.modeId,
                    countGame = base.countGame,
                    winGame = base.winGame,
                    lossGame = base.lossGame,
                    sumTime = base.sumTime,
                    currentStreak = streaks.first,
                    bestStreak = streaks.second,
                    attemptStats = attempts.map {
                        AttemptData(
                            number = "№${it.label}",
                            count = it.count,
                            percent = if (base.winGame > 0) it.count.toFloat() / base.winGame else 0f
                        )
                    },
                    langStats = langs.map {
                        AttemptData(
                            number = it.label,
                            count = it.count,
                            percent = if (base.countGame > 0) it.count.toFloat() / base.countGame else 0f
                        )
                    },
                    lengthStats = lengths.map {
                        AttemptData(
                            number = it.label,
                            count = it.count,
                            percent = if (base.countGame > 0) it.count.toFloat() / base.countGame else 0f
                        )
                    }
                )
            }

            Log.d("DDD", result.toString())
            Result.success(result)
        } catch (e: Exception) {
            Log.d("DDD", e.toString())

            Result.failure(e)
        }
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

    override suspend fun getModes(lang: String): Result<List<ModeStatisticsTranslations>> {
        return try {
            Log.d("DDD", "${offlineStatisticDao.getModesTranslations(lang)}")
            Result.success(
                listOf(
                    ModeStatisticsTranslations(
                        GameMode.ALL.id,
                        lang,
                        GameMode.ALL.res.toString()
                    )
                ) + offlineStatisticDao.getModesTranslations(lang)
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // GameScreen

    override suspend fun getAllStatisticByMode(mode: Int): Result<StatAggregated> {
        return try {
            val streak = offlineStatisticDao.getAllGamesOrdered()
            val streaks = calculateStreaks(streak.filter { it.modeId == mode })
            val base = offlineStatisticDao.getAggregatedByMode(mode)

            val attempts =
                if (mode == GameMode.ALL.id) emptyList() else offlineStatisticDao.getAttemptBreakdown(
                    mode
                )
            val langs =
                if (mode == GameMode.ALL.id) emptyList() else offlineStatisticDao.getLangBreakdown(
                    mode
                )
            val lengths =
                if (mode == GameMode.ALL.id) emptyList() else offlineStatisticDao.getLengthBreakdown(
                    mode
                )


            Result.success(StatAggregated(
                modeId = base.modeId,
                countGame = base.countGame,
                winGame = base.winGame,
                lossGame = base.lossGame,
                sumTime = base.sumTime,
                currentStreak = streaks.first,
                bestStreak = streaks.second,
                attemptStats = attempts.map {
                    AttemptData(
                        "№${it.label}",
                        it.count,
                        if (base.winGame > 0) it.count.toFloat() / base.winGame else 0f
                    )
                },
                langStats = langs.map {
                    AttemptData(
                        it.label,
                        it.count,
                        if (base.countGame > 0) it.count.toFloat() / base.countGame else 0f
                    )
                },
                lengthStats = lengths.map {
                    AttemptData(
                        "${it.label}L",
                        it.count,
                        if (base.countGame > 0) it.count.toFloat() / base.countGame else 0f
                    )
                }
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

            supabaseStatisticsDataSource.syncFromSupabase(user.id).getOrThrow()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncFromLocal(): Result<Unit> {
        return try {
            val user = supabaseAuthDataSource.getCurrentUser()
                ?: return Result.failure(UserNotAuthenticatedException())

            supabaseStatisticsDataSource.syncToSupabase(user.id).getOrThrow()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // AchievementManager

    override suspend fun getCurrentStreak(isWin: Boolean, currentResult: Boolean): Int {
        val games = offlineStatisticDao.getAllGamesOrdered()
        val allGames =
            games + GameRow(modeId = 0, result = if (currentResult) 1 else 0, createdAt = "")
        var streak = 0
        for (game in allGames.reversed()) {
            if ((game.result == 1) == isWin) streak++
            else break
        }
        return streak
    }
}