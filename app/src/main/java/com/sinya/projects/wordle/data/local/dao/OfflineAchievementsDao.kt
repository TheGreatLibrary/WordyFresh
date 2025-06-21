package com.sinya.projects.wordle.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sinya.projects.wordle.domain.model.entity.OfflineAchievements

@Dao
interface OfflineAchievementsDao {

    @Query("SELECT * FROM offline_achievements")
    suspend fun getAchievements(): List<OfflineAchievements>

    @Query("DELETE FROM offline_achievements")
    suspend fun clear()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(row: OfflineAchievements)

    @Query("UPDATE offline_achievements SET count = count + 1 WHERE achieve_id = :id")
    suspend fun increment(id: Int): Int

    @Query("UPDATE offline_achievements SET count = 0 WHERE achieve_id = :id")
    suspend fun resetCount(id: Int)
}