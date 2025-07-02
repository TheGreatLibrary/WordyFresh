package com.sinya.projects.wordle.data.local.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface WordDao {
    @Query("SELECT EXISTS(SELECT 1 FROM words WHERE word = :word AND language = :lang AND length = :len AND (:rating OR rating = 0))")
    suspend fun existsWord(word: String, lang: String, len: Int, rating: Int): Boolean

    @Query("SELECT 1 FROM words WHERE word = :word AND language = :lang AND (rating = 0 OR :rating)")
    suspend fun findWord(word: String, lang: String, rating: Boolean): Int?

    @Query("SELECT word FROM words WHERE length = :length AND language = :lang AND (:rating OR rating = 0) ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomWord(length: Int, lang: String, rating: Boolean): String

    @Query("DELETE FROM words WHERE word IN (:words)")
    suspend fun deleteWords(words: List<String>)

    @Query("SELECT id FROM words WHERE word = :word")
    suspend fun getWordId(word: String) : String

    @Query("SELECT EXISTS(SELECT 1 FROM words WHERE word = :word)")
    suspend fun exists(word: String): Boolean

    @Query("SELECT language FROM words WHERE word = :word LIMIT 1")
    suspend fun getWordLang(word: String): String?

    @Query("SELECT rating FROM words WHERE word = :word LIMIT 1")
    suspend fun getWordRating(word: String): Boolean
}

