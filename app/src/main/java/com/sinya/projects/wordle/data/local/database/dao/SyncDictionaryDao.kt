package com.sinya.projects.wordle.data.local.database.dao

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sinya.projects.wordle.data.remote.supabase.entity.SyncDictionary
import com.sinya.projects.wordle.data.remote.supabase.entity.SyncStatistics

@Dao
interface SyncDictionaryDao {

    // DictionaryScreen

    @Query("DELETE FROM sync_dictionary")
    suspend fun clearAll()

    // SyncManager

    @Query("SELECT id FROM words")
    suspend fun getAllIds(): List<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertList(dictionaryEntity: List<SyncDictionary>)

    @Transaction
    suspend fun replaceAll(list: List<SyncDictionary>) {
        val existingWordIds = getAllIds().toSet()
        val filtered = list.filter { it.wordId in existingWordIds }
        clearAll()
        if (filtered.isNotEmpty()) insertList(filtered)
    }
}