package com.sinya.projects.wordle.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sinya.projects.wordle.domain.model.data.DictionaryItem
import com.sinya.projects.wordle.domain.model.entity.OfflineDictionary

@Dao
interface OfflineDictionaryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWord(dictionaryEntity: OfflineDictionary)

    @Query("UPDATE offline_dictionary SET description = :description WHERE word_id = :wordId")
    suspend fun updateDescription(wordId: String, description: String) : Int

    @Query("SELECT 1 FROM offline_dictionary d JOIN words w ON d.word_id=w.id WHERE w.word = :word")
    suspend fun findWord(word: String): Int?

    @Query("SELECT d.id, w.word, d.description FROM offline_dictionary d JOIN words w ON d.word_id=w.id")
    suspend fun getAllWords(): List<DictionaryItem>

    @Query("SELECT * FROM offline_dictionary")
    suspend fun getDictionary(): List<OfflineDictionary>

    @Query("SELECT language FROM words WHERE word = :word")
    suspend fun getLangForWord(word: String): String?

    @Query("DELETE FROM offline_dictionary")
    suspend fun clear()

    @Transaction
    suspend fun insertOrUpdateDescription(wordId: String, description: String) {
        val updated = updateDescription(wordId, description)
        if (updated == 0) {
            insertWord(OfflineDictionary(id = wordId, wordId = wordId, description = description))
        }
    }
}