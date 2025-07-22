package com.sinya.projects.wordle.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sinya.projects.wordle.screen.dictionary.DictionaryItem
import com.sinya.projects.wordle.data.supabase.entity.SyncDictionary

@Dao
interface SyncDictionaryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWord(dictionaryEntity: SyncDictionary)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertList(dictionaryEntity: List<SyncDictionary>)

    @Query("UPDATE sync_dictionary SET description = :description WHERE word_id = :wordId")
    suspend fun updateDescription(wordId: String, description: String)

    @Query("SELECT 1 FROM sync_dictionary d JOIN words w ON d.word_id=w.id WHERE w.word = :word")
    suspend fun findWord(word: String): Int?

    @Query("SELECT d.word_id as id, w.word, d.description FROM sync_dictionary d JOIN words w ON d.word_id=w.id")
    suspend fun getAllWords(): List<DictionaryItem>

    @Query("SELECT language FROM words WHERE word = :word")
    suspend fun getLangForWord(word: String): String?

    @Query("DELETE FROM sync_dictionary")
    suspend fun clearAll()
}