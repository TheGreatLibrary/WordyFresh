package com.sinya.projects.wordle.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sinya.projects.wordle.domain.model.entity.Profiles

@Dao
interface ProfilesDao {
    @Query("SELECT id FROM profiles LIMIT 1")
    suspend fun getProfileId(): String

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProfile(user: Profiles)

    @Update
    suspend fun updateProfile(user: Profiles)

    @Query("SELECT * FROM profiles WHERE id = :id")
    suspend fun getProfileById(id: String): Profiles

    @Query("DELETE FROM profiles")
    suspend fun clear()
}