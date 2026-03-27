package com.sinya.projects.wordle.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sinya.projects.wordle.data.local.database.entity.ModeStatisticsTranslations
import com.sinya.projects.wordle.data.local.database.entity.ModesStatistics
import com.sinya.projects.wordle.data.local.database.entity.OfflineStatistics
import com.sinya.projects.wordle.domain.model.GameRow
import com.sinya.projects.wordle.domain.model.StatAggregated
import com.sinya.projects.wordle.domain.model.StatAggregatedEntity
import com.sinya.projects.wordle.domain.model.StatBreakdown

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

    @Query("""
        SELECT *
        FROM mode_statistics_translations
        WHERE lang = :lang
    """)
    suspend fun getModesTranslations(lang: String): List<ModeStatisticsTranslations>

    @Query("SELECT * FROM modes_statistics")
    suspend fun getModes(): List<ModesStatistics>

    @Query("""
           SELECT
            mode_id as modeId,
            COUNT(*) AS countGame,
            SUM(CASE WHEN result = 1 THEN 1 ELSE 0 END) AS winGame,
            SUM(CASE WHEN result = 0 THEN 1 ELSE 0 END) AS lossGame,
            SUM(time_game) AS sumTime,
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
    suspend fun getAggregatedByMode(modeId: Int): StatAggregatedEntity

    @Query("""
           SELECT
            :modeId as modeId,
            COUNT(*) AS countGame,
            SUM(CASE WHEN result = 1 THEN 1 ELSE 0 END) AS winGame,
            SUM(CASE WHEN result = 0 THEN 1 ELSE 0 END) AS lossGame,
            SUM(time_game) AS sumTime,
             0 AS currentStreak,
            0 AS bestStreak
        FROM (
            SELECT mode_id, result, try_number, time_game FROM offline_statistics
            UNION ALL
            SELECT mode_id, result, try_number, time_game FROM sync_statistics
        )
    """)
    suspend fun getAggregatedTotal(modeId: Int): StatAggregatedEntity

    @Query("""
        SELECT
            mode_id as modeId,
            COUNT(*) AS countGame,
            SUM(CASE WHEN result = 1 THEN 1 ELSE 0 END) AS winGame,
            SUM(CASE WHEN result = 0 THEN 1 ELSE 0 END) AS lossGame,
            SUM(time_game) AS sumTime,
            0 AS currentStreak,
            0 AS bestStreak
        FROM (
            SELECT mode_id, result, try_number, time_game FROM offline_statistics
            UNION ALL
            SELECT mode_id, result, try_number, time_game FROM sync_statistics
        )
        GROUP BY mode_id
    """)
    suspend fun getAggregatedAll(): List<StatAggregatedEntity>

    @Query("""
    WITH all_attempts(label) AS (
        VALUES (1), (2), (3), (4), (5), (6)
    )
    SELECT a.label, COALESCE(COUNT(s.try_number), 0) as count
    FROM all_attempts a
    LEFT JOIN (
        SELECT try_number FROM offline_statistics WHERE mode_id = :modeId
        UNION ALL
        SELECT try_number FROM sync_statistics WHERE mode_id = :modeId
    ) s ON s.try_number = a.label
    GROUP BY a.label ORDER BY a.label
""")
    suspend fun getAttemptBreakdown(modeId: Int): List<StatBreakdown>

    @Query("""
    WITH all_attempts(label) AS (
        VALUES (1), (2), (3), (4), (5), (6)
    )
    SELECT a.label, COALESCE(COUNT(s.try_number), 0) as count
    FROM all_attempts a
    LEFT JOIN (
        SELECT try_number FROM offline_statistics
        UNION ALL
        SELECT try_number FROM sync_statistics
    ) s ON s.try_number = a.label
    GROUP BY a.label ORDER BY a.label
""")
    suspend fun getTotalAttemptBreakdown(): List<StatBreakdown>

    @Query("""
    SELECT w.language as label, COALESCE(COUNT(s.word_lang), 0) as count
    FROM (SELECT DISTINCT language FROM words) w 
    LEFT JOIN (
        SELECT word_lang FROM offline_statistics WHERE mode_id = :modeId
        UNION ALL
        SELECT word_lang FROM sync_statistics WHERE mode_id = :modeId
       ) s ON w.language = s.word_lang
    GROUP BY w.language
""")
    suspend fun getLangBreakdown(modeId: Int): List<StatBreakdown>

    @Query("""
    SELECT w.language as label,COALESCE(COUNT(s.word_lang), 0) as count
   FROM (SELECT DISTINCT language FROM words) w 
   LEFT JOIN (
        SELECT word_lang FROM offline_statistics
        UNION ALL
        SELECT word_lang FROM sync_statistics
    ) s ON w.language = s.word_lang
    GROUP BY w.language
""")
    suspend fun getTotalLangBreakdown(): List<StatBreakdown>

    @Query("""
    SELECT w.length as label, COALESCE(COUNT(s.word_length), 0) as count
    FROM (SELECT DISTINCT length FROM words) w
    LEFT JOIN (
        SELECT word_length FROM offline_statistics WHERE mode_id = :modeId
        UNION ALL
        SELECT word_length FROM sync_statistics WHERE mode_id = :modeId
    ) s ON w.length = s.word_length
    GROUP BY w.length ORDER BY w.length
""")
    suspend fun getLengthBreakdown(modeId: Int): List<StatBreakdown>

    @Query("""
    SELECT w.length as label, COALESCE(COUNT(s.word_length), 0) as count
     FROM (SELECT DISTINCT length FROM words) w
    LEFT JOIN (
        SELECT word_length FROM offline_statistics
        UNION ALL
        SELECT word_length FROM sync_statistics
    ) s ON w.length = s.word_length
    GROUP BY w.length ORDER BY w.length
""")
    suspend fun getTotalLengthBreakdown(): List<StatBreakdown>

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

