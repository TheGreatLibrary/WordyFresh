package com.sinya.projects.wordle.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sinya.projects.wordle.data.local.database.entity.OfflineAchievements
import com.sinya.projects.wordle.data.remote.supabase.entity.SyncAchievements
import com.sinya.projects.wordle.data.remote.supabase.entity.SyncDictionary

@Dao
interface OfflineAchievementsDao {

    @Query("SELECT * FROM offline_achievements")
    suspend fun getAchievements(): List<OfflineAchievements>

    @Query("DELETE FROM offline_achievements")
    suspend fun clearAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(row: OfflineAchievements)

    @Query("UPDATE offline_achievements SET count = :by WHERE achieve_id = :id")
    suspend fun setCount(id: Int, by: Int): Int

    @Query("UPDATE offline_achievements SET count = count + :by WHERE achieve_id = :id")
    suspend fun increment(id: Int, by: Int): Int

    @Query("UPDATE offline_achievements SET count = 0 WHERE achieve_id = :id")
    suspend fun resetCount(id: Int)

    @Transaction
    suspend fun moveOfflineToSync(it: OfflineAchievements) {
        updateSyncCount(it.achieveId, it.count)
        deleteOfflineAchievement(it.achieveId)
    }

    @Query("DELETE FROM offline_achievements WHERE achieve_id = :id")
    suspend fun deleteOfflineAchievement(id: Int)

    @Query("UPDATE sync_achievements SET count = count + :offline WHERE achieve_id = :id")
    suspend fun updateSyncCount(id: Int, offline: Int)
}