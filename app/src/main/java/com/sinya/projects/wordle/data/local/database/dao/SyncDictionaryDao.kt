package com.sinya.projects.wordle.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sinya.projects.wordle.data.remote.supabase.entity.SyncDictionary

@Dao
interface SyncDictionaryDao {

    // DictionaryScreen

    @Query("DELETE FROM sync_dictionary")
    suspend fun clearAll()

    // SyncViewModel

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertList(dictionaryEntity: List<SyncDictionary>)
}