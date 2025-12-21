package com.sinya.projects.wordle.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.sinya.projects.wordle.domain.model.AchieveItem

@Dao
interface AchievementsDao {
    @Query(
    """
        SELECT 
            a.id AS id,
            c.title AS categoryName,
            a.title AS title,
            a.description AS description,
            a.condition AS condition,
            a.image AS image,
            (COALESCE(s.count, 0) + COALESCE(o.count, 0)) AS count,
            a.max_count AS maxCount
        FROM achievements a
        JOIN categories_achieves c ON a.category_id = c.id
        LEFT JOIN sync_achievements s ON a.id = s.achieve_id
        LEFT JOIN offline_achievements o ON a.id = o.achieve_id
    """
    )
    suspend fun getAchievementsList(): List<AchieveItem>
}

