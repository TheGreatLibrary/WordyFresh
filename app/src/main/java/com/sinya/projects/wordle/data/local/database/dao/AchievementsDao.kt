package com.sinya.projects.wordle.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.sinya.projects.wordle.domain.model.AchieveItem
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementsDao {
    @Query("""
        SELECT 
            a.id AS id,
            cat.name AS categoryName,
            at.title AS title,
            at.description AS description,
            at.condition AS condition,
            a.hidden AS hidden,
            a.image AS image,
            MIN(COALESCE(s.count, 0) + COALESCE(o.count, 0), a.max_count) AS count,
            a.max_count AS maxCount
        FROM achievements a
         JOIN achievement_translations at ON a.id = at.achieve_id
        JOIN category_achieve_translations cat ON a.category_id = cat.category_id
        LEFT JOIN sync_achievements s ON a.id = s.achieve_id
        LEFT JOIN offline_achievements o ON a.id = o.achieve_id
        WHERE at.lang = :lang AND cat.lang = :lang
        
    """)
    fun observeAchievements(lang: String): Flow<List<AchieveItem>>

    @Query("""
        SELECT 
            a.id AS id,
            cat.name AS categoryName,
            at.title AS title,
            at.description AS description,
            at.condition AS condition,
            a.hidden AS hidden,
            a.image AS image,
            (COALESCE(s.count, 0) + COALESCE(o.count, 0)) AS count,
            a.max_count AS maxCount
        FROM achievements a
        JOIN achievement_translations at ON a.id = at.achieve_id
        JOIN category_achieve_translations cat ON a.category_id = cat.category_id
        LEFT JOIN sync_achievements s ON a.id = s.achieve_id
        LEFT JOIN offline_achievements o ON a.id = o.achieve_id
        WHERE at.lang = :lang AND cat.lang = :lang
    """)
    suspend fun getAchievementsList(lang: String): List<AchieveItem>
}

