package com.sinya.projects.wordle.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sinya.projects.wordle.data.supabase.entity.Profiles

@Dao
interface ProfilesDao {
    @Query("SELECT id FROM profiles LIMIT 1")
    suspend fun getProfileId(): String

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProfile(user: Profiles)

    @Update
    suspend fun updateProfile(user: Profiles)

    @Query("""
        UPDATE profiles 
        SET nickname = :nickname 
        WHERE id = :id
    """)
    suspend fun updateProfile(nickname: String, id: String)

    @Query("SELECT * FROM profiles WHERE id = :id")
    suspend fun getProfileById(id: String): Profiles

    @Query("DELETE FROM profiles")
    suspend fun clearAll()
}