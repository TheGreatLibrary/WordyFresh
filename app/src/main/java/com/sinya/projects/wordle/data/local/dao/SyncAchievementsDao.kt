package com.sinya.projects.wordle.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sinya.projects.wordle.data.supabase.entity.SyncAchievements

@Dao
interface SyncAchievementsDao {

    @Query("DELETE FROM sync_achievements")
    suspend fun clearAll()

    @Query("""
        UPDATE sync_achievements
        SET count = :count, updated_at = :updatedAt
        WHERE user_id = :userId AND achieve_id = :id
    """)
    suspend fun updateFields(id: Int, userId: String, count: Int, updatedAt: String)

    @Transaction
    suspend fun updateAchievementsList(list: List<SyncAchievements>) {
        list.forEach { dto ->
            updateFields(dto.achieveId, dto.userId, dto.count, dto.updatedAt)
        }
    }

    @Query("SELECT * FROM sync_achievements")
    suspend fun getAll(): List<SyncAchievements>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(list: List<SyncAchievements>)
}