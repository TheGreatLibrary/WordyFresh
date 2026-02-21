package com.sinya.projects.wordle.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.sinya.projects.wordle.data.local.database.entity.OfflineDictionary
import com.sinya.projects.wordle.domain.model.DictionaryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface OfflineDictionaryDao {

    // DictionaryScreen

    @Query("""
    SELECT 
        COALESCE(od.word_id, sd.word_id) as id,
        w.word,
        COALESCE(od.description, sd.description) as description
    FROM words w
    LEFT JOIN offline_dictionary od ON w.id = od.word_id
    LEFT JOIN sync_dictionary sd ON w.id = sd.word_id
    WHERE od.word_id IS NOT NULL OR sd.word_id IS NOT NULL
""")
    fun getAllWords(): Flow<List<DictionaryItem>>

    @Query("DELETE FROM offline_dictionary")
    suspend fun clearAll()

    // GameScreen

    @Query("SELECT 1 FROM offline_dictionary d JOIN words w ON d.word_id=w.id WHERE w.word = :word")
    suspend fun findWord(word: String): Int?

    @Upsert
    suspend fun insertWord(dictionaryEntity: OfflineDictionary)

    @Query("UPDATE offline_dictionary SET description = :description WHERE word_id = :wordId")
    suspend fun updateDescription(wordId: Int, description: String) : Int

    @Transaction
    suspend fun insertOrUpdateDescription(wordId: Int, description: String): Int {
        val updated = updateDescription(wordId, description)
        if (updated == 0) {
            insertWord(OfflineDictionary(wordId = wordId, description = description))
        }
        return updated
    }

    // SyncViewModel

    @Query("SELECT * FROM offline_dictionary")
    suspend fun getDictionary(): List<OfflineDictionary>
}