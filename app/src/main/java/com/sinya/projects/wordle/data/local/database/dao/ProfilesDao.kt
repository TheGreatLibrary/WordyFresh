package com.sinya.projects.wordle.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sinya.projects.wordle.data.remote.supabase.entity.Profiles

@Dao
interface ProfilesDao {
    @Query("SELECT id FROM profiles LIMIT 1")
    suspend fun getProfileId(): String

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(user: Profiles)

    @Update
    suspend fun updateProfile(user: Profiles)

    @Query("""
        UPDATE profiles 
        SET nickname = :nickname 
        WHERE id = :id
    """)
    suspend fun updateNickname(nickname: String, id: String)

    @Query("SELECT * FROM profiles LIMIT 1")
    suspend fun getLocalFirstProfile(): Profiles?

    @Query("SELECT * FROM profiles WHERE id = :id")
    suspend fun getProfileById(id: String): Profiles?

    @Query("DELETE FROM profiles")
    suspend fun clearAll()

    @Query(
        """
        UPDATE profiles 
        SET avatar_url = :img 
        WHERE id = :userId
    """
    )
    suspend fun updateImageProfile(img: String, userId: String)
}