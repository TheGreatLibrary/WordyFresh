package com.sinya.projects.wordle.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.sinya.projects.wordle.domain.model.entity.OfflineAchievements

@Dao
interface OfflineAchievementsDao {

    @Query("SELECT * FROM offline_achievements")
    suspend fun getAchievements(): List<OfflineAchievements>

    @Query("DELETE FROM offline_achievements")
    suspend fun clear()
}