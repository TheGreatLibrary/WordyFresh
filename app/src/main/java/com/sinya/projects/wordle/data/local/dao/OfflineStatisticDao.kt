package com.sinya.projects.wordle.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sinya.projects.wordle.data.local.entity.Modes
import com.sinya.projects.wordle.data.local.entity.OfflineStatistic

@Dao
interface OfflineStatisticDao {

    @Query("""
        UPDATE offline_statistic
        SET 
            count_game = :countGame,
            current_streak = :currentStreak,
            best_streak = :bestStreak,
            win_game = :winGame,
            sum_time = :sumTime,
            first_try = :firstTry,
            second_try = :secondTry,
            third_try = :thirdTry,
            fourth_try = :fourthTry,
            fifth_try = :fifthTry,
            sixth_try = :sixthTry
        WHERE mode_id = :modeId
    """)
    suspend fun updateStatisticMode(
        modeId: Int,
        countGame: Int,
        currentStreak: Int,
        bestStreak: Int,
        winGame: Int,
        sumTime: Long,
        firstTry: Int,
        secondTry: Int,
        thirdTry: Int,
        fourthTry: Int,
        fifthTry: Int,
        sixthTry: Int
    )

    @Update
    suspend fun updateStatistic(statistic: OfflineStatistic)

    @Insert
    suspend fun insertStatisticList(list: List<OfflineStatistic>)

    @Query("SELECT COUNT(*) FROM offline_statistic")
    suspend fun count(): Int

    @Query("SELECT * FROM offline_statistic WHERE mode_id = :modeId")
    suspend fun getStatisticByMode(modeId: Int): OfflineStatistic

    @Query("SELECT * FROM offline_statistic")
    suspend fun getAllStatistic(): List<OfflineStatistic>

    @Query("DELETE FROM offline_statistic")
    suspend fun clearAll()

    @Query("SELECT * FROM modes")
    suspend fun getModes() : List<Modes>

    @Query("""
      SELECT
        o.mode_id                               AS mode_id,
        o.count_game + COALESCE(s.count_game,0) AS count_game,
        o.current_streak + COALESCE(s.current_streak,0) AS current_streak,
        max(o.best_streak, COALESCE(s.best_streak,0))   AS best_streak,
        o.win_game + COALESCE(s.win_game,0)     AS win_game,
        o.sum_time + COALESCE(s.sum_time,0)     AS sum_time,
        o.first_try + COALESCE(s.first_try,0)   AS first_try,
        o.second_try + COALESCE(s.second_try,0) AS second_try,
        o.third_try + COALESCE(s.third_try,0)   AS third_try,
        o.fourth_try + COALESCE(s.fourth_try,0) AS fourth_try,
        o.fifth_try + COALESCE(s.fifth_try,0)   AS fifth_try,
        o.sixth_try + COALESCE(s.sixth_try,0)   AS sixth_try
      FROM offline_statistic o
      LEFT JOIN sync_statistic s ON o.mode_id = s.mode_id
    """)
    suspend fun getMergedStatsList(): List<OfflineStatistic>

    @Query("""
      SELECT 
        -1                        AS mode_id,
        SUM(o.count_game + COALESCE(s.count_game,0)) AS count_game,
        SUM(o.current_streak + COALESCE(s.current_streak,0)) AS current_streak,
        MAX( max(o.best_streak, COALESCE(s.best_streak,0)) ) AS best_streak,
        SUM(o.win_game + COALESCE(s.win_game,0))       AS win_game,
        SUM(o.sum_time + COALESCE(s.sum_time,0))       AS sum_time,
        SUM(o.first_try + COALESCE(s.first_try,0))     AS first_try,
        SUM(o.second_try + COALESCE(s.second_try,0))   AS second_try,
        SUM(o.third_try + COALESCE(s.third_try,0))     AS third_try,
        SUM(o.fourth_try + COALESCE(s.fourth_try,0))   AS fourth_try,
        SUM(o.fifth_try + COALESCE(s.fifth_try,0))     AS fifth_try,
        SUM(o.sixth_try + COALESCE(s.sixth_try,0))     AS sixth_try
      FROM offline_statistic o
      LEFT JOIN sync_statistic s ON o.mode_id = s.mode_id
    """)
    suspend fun getMergedSummary(): OfflineStatistic
}

