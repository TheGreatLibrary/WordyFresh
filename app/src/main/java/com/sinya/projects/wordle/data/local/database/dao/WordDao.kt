package com.sinya.projects.wordle.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.sinya.projects.wordle.data.local.database.entity.Words

@Dao
interface WordDao {
    @Query("SELECT EXISTS(SELECT 1 FROM words WHERE word = :word AND language = :lang AND length = :len AND (:rating OR rating = 0))")
    suspend fun existsWord(word: String, lang: String, len: Int, rating: Int): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM words WHERE word = :word)")
    suspend fun existsWord(word: String): Boolean

    @Query("SELECT word FROM words WHERE length = :length AND language = :lang AND (:rating OR rating = 0) ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomWord(length: Int, lang: String, rating: Boolean): String

    @Query("DELETE FROM words WHERE word IN (:words)")
    suspend fun deleteWords(words: List<String>)

    @Query("SELECT * FROM words WHERE word = :word")
    suspend fun getWord(word: String) : Words?

    @Query("SELECT EXISTS(SELECT 1 FROM words WHERE word = :word)")
    suspend fun exists(word: String): Boolean

    @Query("SELECT language FROM words WHERE word = :word LIMIT 1")
    suspend fun getWordLang(word: String): String?

    @Query("SELECT rating FROM words WHERE word = :word LIMIT 1")
    suspend fun getWordRating(word: String): Boolean

    @Query("SELECT word FROM words WHERE id = :wordId")
    suspend fun getWordById(wordId: Int): String
}

