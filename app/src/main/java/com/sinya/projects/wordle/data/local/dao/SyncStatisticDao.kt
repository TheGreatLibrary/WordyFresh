package com.sinya.projects.wordle.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sinya.projects.wordle.data.remote.supabase.entity.SyncStatistic

@Dao
interface SyncStatisticDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertList(dictionaryEntity: List<SyncStatistic>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(list: List<SyncStatistic>)

    @Query("SELECT * FROM sync_statistic")
    suspend fun getAllStatistic(): List<SyncStatistic>

    @Query("DELETE FROM sync_statistic")
    suspend fun clearAll()
}