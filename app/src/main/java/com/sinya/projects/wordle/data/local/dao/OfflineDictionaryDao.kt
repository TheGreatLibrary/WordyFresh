package com.sinya.projects.wordle.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sinya.projects.wordle.screen.dictionary.DictionaryItem
import com.sinya.projects.wordle.data.local.entity.OfflineDictionary

@Dao
interface OfflineDictionaryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWord(dictionaryEntity: OfflineDictionary)

    @Query("UPDATE offline_dictionary SET description = :description WHERE word_id = :wordId")
    suspend fun updateDescription(wordId: Int, description: String) : Int

    @Query("SELECT 1 FROM offline_dictionary d JOIN words w ON d.word_id=w.id WHERE w.word = :word")
    suspend fun findWord(word: String): Int?

    @Query("SELECT d.word_id as id, w.word, d.description FROM offline_dictionary d JOIN words w ON d.word_id=w.id")
    suspend fun getAllWords(): List<DictionaryItem>

    @Query("SELECT * FROM offline_dictionary")
    suspend fun getDictionary(): List<OfflineDictionary>

    @Query("SELECT language FROM words WHERE word = :word")
    suspend fun getLangForWord(word: String): String?

    @Query("DELETE FROM offline_dictionary")
    suspend fun clearAll()

    @Transaction
    suspend fun insertOrUpdateDescription(wordId: Int, description: String) {
        val updated = updateDescription(wordId, description)
        if (updated == 0) {
            insertWord(OfflineDictionary(wordId = wordId, description = description))
        }
    }


    @Query("""
    SELECT 
        d.word_id as id, 
        w.word, 
        d.description 
    FROM (
        SELECT word_id, word_id, description FROM offline_dictionary
        UNION
        SELECT word_id, word_id, description FROM sync_dictionary
    ) AS d
    JOIN words w ON d.word_id = w.id
""")
    suspend fun getMergedSummary(): List<DictionaryItem>
}