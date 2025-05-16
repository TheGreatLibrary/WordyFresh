package com.sinya.projects.wordle.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.sinya.projects.wordle.domain.model.supabase.SyncAchievements

@Dao
interface SyncAchievementsDao {

    @Query("DELETE FROM sync_achievements")
    suspend fun clear()

    @Query("""
        UPDATE sync_achievements
        SET count = :count, updated_at = :updatedAt
        WHERE user_id = :userId AND id = :id
    """)
    suspend fun updateFields(id: String, userId: String, count: Int, updatedAt: String)

    @Transaction
    suspend fun updateAchievementsList(list: List<SyncAchievements>) {
        list.forEach { dto ->
            updateFields(dto.id, dto.userId, dto.count, dto.updatedAt)
        }
    }
}