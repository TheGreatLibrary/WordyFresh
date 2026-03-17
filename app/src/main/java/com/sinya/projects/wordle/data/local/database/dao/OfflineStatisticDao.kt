package com.sinya.projects.wordle.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sinya.projects.wordle.data.local.database.entity.ModesStatistics
import com.sinya.projects.wordle.data.local.database.entity.OfflineStatistics
import com.sinya.projects.wordle.domain.model.GameRow
import com.sinya.projects.wordle.domain.model.StatAggregated

@Dao
interface OfflineStatisticDao {

    // GameViewModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(stat: OfflineStatistics)

    @Query("SELECT * FROM offline_statistics")
    suspend fun getAllStatistic(): List<OfflineStatistics>

    @Query("SELECT * FROM offline_statistics WHERE mode_id = :modeId")
    suspend fun getStatisticByMode(modeId: Int): List<OfflineStatistics>

    @Query("DELETE FROM offline_statistics")
    suspend fun clearAll()

    @Query("SELECT * FROM modes_statistics")
    suspend fun getModes(): List<ModesStatistics>

    @Query("""
           SELECT
            mode_id as modeId,
            COUNT(*) AS countGame,
            SUM(CASE WHEN result = 1 THEN 1 ELSE 0 END) AS winGame,
            SUM(CASE WHEN result = 0 THEN 1 ELSE 0 END) AS lossGame,
            SUM(time_game) AS sumTime,
            SUM(CASE WHEN try_number = 1 THEN 1 ELSE 0 END) AS firstTry,
            SUM(CASE WHEN try_number = 2 THEN 1 ELSE 0 END) AS secondTry,
            SUM(CASE WHEN try_number = 3 THEN 1 ELSE 0 END) AS thirdTry,
            SUM(CASE WHEN try_number = 4 THEN 1 ELSE 0 END) AS fourthTry,
            SUM(CASE WHEN try_number = 5 THEN 1 ELSE 0 END) AS fifthTry,
            SUM(CASE WHEN try_number = 6 THEN 1 ELSE 0 END) AS sixthTry,
             0 AS currentStreak,
            0 AS bestStreak
        FROM (
            SELECT mode_id, result, try_number, time_game FROM offline_statistics
            UNION ALL
            SELECT mode_id, result, try_number, time_game FROM sync_statistics
        )
        WHERE mode_id = :modeId
        GROUP BY mode_id
    """)
    suspend fun getAggregatedByMode(modeId: Int): StatAggregated

    @Query("""
           SELECT
            :modeId as modeId,
            COUNT(*) AS countGame,
            SUM(CASE WHEN result = 1 THEN 1 ELSE 0 END) AS winGame,
            SUM(CASE WHEN result = 0 THEN 1 ELSE 0 END) AS lossGame,
            SUM(time_game) AS sumTime,
            SUM(CASE WHEN try_number = 1 THEN 1 ELSE 0 END) AS firstTry,
            SUM(CASE WHEN try_number = 2 THEN 1 ELSE 0 END) AS secondTry,
            SUM(CASE WHEN try_number = 3 THEN 1 ELSE 0 END) AS thirdTry,
            SUM(CASE WHEN try_number = 4 THEN 1 ELSE 0 END) AS fourthTry,
            SUM(CASE WHEN try_number = 5 THEN 1 ELSE 0 END) AS fifthTry,
            SUM(CASE WHEN try_number = 6 THEN 1 ELSE 0 END) AS sixthTry,
             0 AS currentStreak,
            0 AS bestStreak
        FROM (
            SELECT mode_id, result, try_number, time_game FROM offline_statistics
            UNION ALL
            SELECT mode_id, result, try_number, time_game FROM sync_statistics
        )
    """)
    suspend fun getAggregatedTotal(modeId: Int): StatAggregated

    @Query("""
        SELECT
            mode_id as modeId,
            COUNT(*) AS countGame,
            SUM(CASE WHEN result = 1 THEN 1 ELSE 0 END) AS winGame,
            SUM(CASE WHEN result = 0 THEN 1 ELSE 0 END) AS lossGame,
            SUM(time_game) AS sumTime,
            SUM(CASE WHEN try_number = 1 THEN 1 ELSE 0 END) AS firstTry,
            SUM(CASE WHEN try_number = 2 THEN 1 ELSE 0 END) AS secondTry,
            SUM(CASE WHEN try_number = 3 THEN 1 ELSE 0 END) AS thirdTry,
            SUM(CASE WHEN try_number = 4 THEN 1 ELSE 0 END) AS fourthTry,
            SUM(CASE WHEN try_number = 5 THEN 1 ELSE 0 END) AS fifthTry,
            SUM(CASE WHEN try_number = 6 THEN 1 ELSE 0 END) AS sixthTry,
            0 AS currentStreak,
            0 AS bestStreak
        FROM (
            SELECT mode_id, result, try_number, time_game FROM offline_statistics
            UNION ALL
            SELECT mode_id, result, try_number, time_game FROM sync_statistics
        )
        GROUP BY mode_id
    """)
    suspend fun getAggregatedAll(): List<StatAggregated>

    @Query("""
        SELECT 
            mode_id AS modeId, 
            result, 
            created_at AS createdAt 
        FROM (
            SELECT mode_id, result, created_at FROM offline_statistics
            UNION ALL
            SELECT mode_id, result, created_at FROM sync_statistics
        ) ORDER BY created_at ASC
    """)
    suspend fun getAllGamesOrdered(): List<GameRow>
}