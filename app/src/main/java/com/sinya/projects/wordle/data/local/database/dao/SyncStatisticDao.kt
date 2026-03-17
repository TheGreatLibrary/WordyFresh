package com.sinya.projects.wordle.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sinya.projects.wordle.data.remote.supabase.entity.SyncStatistics

@Dao
interface SyncStatisticDao {

    @Query("SELECT * FROM sync_statistics")
    suspend fun getAllStatistic(): List<SyncStatistics>

    @Query("SELECT * FROM sync_statistics WHERE mode_id = :modeId")
    suspend fun getStatisticByMode(modeId: Int): SyncStatistics

    @Transaction
    suspend fun replaceAll(list: List<SyncStatistics>) {
        clearAll()
        if (list.isNotEmpty()) {
            insertStatistic(list)
        }
    }

    @Query("DELETE FROM sync_statistics")
    suspend fun clearAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatistic(list: List<SyncStatistics>)
}