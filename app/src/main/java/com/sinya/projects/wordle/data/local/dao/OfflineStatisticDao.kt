package com.sinya.projects.wordle.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sinya.projects.wordle.domain.model.entity.OfflineStatistic

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
        modeId: String,
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
    suspend fun getStatisticByMode(modeId: String): OfflineStatistic

    @Query("SELECT * FROM offline_statistic")
    suspend fun getAllStatistic(): List<OfflineStatistic>

    @Query("DELETE FROM offline_statistic")
    suspend fun clear()
}