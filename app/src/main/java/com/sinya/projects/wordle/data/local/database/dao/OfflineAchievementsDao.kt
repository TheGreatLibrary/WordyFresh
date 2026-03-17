package com.sinya.projects.wordle.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sinya.projects.wordle.data.local.database.entity.OfflineAchievements

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
}