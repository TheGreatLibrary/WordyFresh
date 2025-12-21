package com.sinya.projects.wordle.domain.repository

import android.util.Log
import com.sinya.projects.wordle.domain.checker.NetworkChecker
import com.sinya.projects.wordle.domain.error.DefinitionNotFoundException
import com.sinya.projects.wordle.domain.error.NoInternetException
import com.sinya.projects.wordle.data.local.database.dao.OfflineDictionaryDao
import com.sinya.projects.wordle.data.local.database.dao.SyncDictionaryDao
import com.sinya.projects.wordle.data.local.database.dao.WordDao
import com.sinya.projects.wordle.data.local.database.entity.OfflineDictionary
import com.sinya.projects.wordle.data.local.database.entity.Words
import com.sinya.projects.wordle.domain.error.UserNotAuthenticatedException
import com.sinya.projects.wordle.domain.model.DictionaryItem
import com.sinya.projects.wordle.domain.source.DictionaryDataSource
import com.sinya.projects.wordle.domain.source.SupabaseAuthDataSource
import com.sinya.projects.wordle.domain.source.SupabaseDictionaryDataSource
import jakarta.inject.Inject

interface DictionaryRepository {
    suspend fun insertOrUpdateDescription(item: OfflineDictionary)
    suspend fun getAllWords(): List<DictionaryItem>
    suspend fun getDefinitionForWord(word: String): Result<String>
    suspend fun syncFromSupabase(): Result<Unit>
    suspend fun syncFromLocal(): Result<Unit>
    suspend fun clearLocal()
    suspend fun clearAllDictionary()

    suspend fun getLangForWord(word: String): String?
    suspend fun getWordRating(word: String): Result<Boolean>
    suspend fun getRandomWord(length: Int, lang: String, ratingStatus: Boolean): Result<String>
    suspend fun getWord(word: String): Words?
    suspend fun existWord(word: String, lang: String, length: Int, ratingStatus: Int): Boolean
}

class DictionaryRepositoryImpl @Inject constructor(
    private val offlineDictionaryDao: OfflineDictionaryDao,
    private val syncDictionaryDao: SyncDictionaryDao,
    private val wordDao: WordDao,
    private val dictionaryDataSource: DictionaryDataSource,
    private val networkChecker: NetworkChecker,
    private val supabaseAuthDataSource: SupabaseAuthDataSource,
    private val supabaseDictionaryDataSource: SupabaseDictionaryDataSource
) : DictionaryRepository {

    override suspend fun clearAllDictionary() {
        offlineDictionaryDao.clearAll()

        val user = supabaseAuthDataSource.getCurrentUser()
        if (user != null) {
            supabaseDictionaryDataSource.clearAllDictionary(user.id)
        } else {
            Log.d("StatisticRepository", "User not authenticated, skipping remote clear")
        }
    }

    override suspend fun syncFromSupabase(): Result<Unit> {
        return try {
            val user = supabaseAuthDataSource.getCurrentUser()
                ?: return Result.failure(UserNotAuthenticatedException())

            supabaseDictionaryDataSource.syncFromSupabase(user.id)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncFromLocal(): Result<Unit> {
        return try {
            val user = supabaseAuthDataSource.getCurrentUser()
                ?: return Result.failure(UserNotAuthenticatedException())

            supabaseDictionaryDataSource.syncToSupabase(user.id)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun clearLocal() {
        offlineDictionaryDao.clearAll()
        syncDictionaryDao.clearAll()
    }

    override suspend fun insertOrUpdateDescription(item: OfflineDictionary) {
        offlineDictionaryDao.insertOrUpdateDescription(item.wordId, item.description)
    }

    override suspend fun getAllWords(): List<DictionaryItem> {
        val offline = offlineDictionaryDao.getAllWords()
        val sync = syncDictionaryDao.getAllWords()
        return mergeDictionary(offline, sync)
    }

    override suspend fun getDefinitionForWord(word: String): Result<String> {
        if (!networkChecker.isInternetAvailable()) {
            return Result.failure(NoInternetException())
        }

        val lang = getLangForWord(word) ?: "ru"

        val wikipediaResult = dictionaryDataSource.getDefinition(word, lang)
        if (wikipediaResult.isSuccess) {
            return wikipediaResult
        }

        return Result.failure(DefinitionNotFoundException())
    }

    override suspend fun getLangForWord(word: String): String? {
        return wordDao.getWordLang(word)
    }

    override suspend fun getWordRating(word: String): Result<Boolean> {
        return try {
            Result.success(wordDao.getWordRating(word))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRandomWord(
        length: Int,
        lang: String,
        ratingStatus: Boolean
    ): Result<String> {
        return try {
            Result.success(wordDao.getRandomWord(length, lang, ratingStatus))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getWord(word: String): Words? {
        return wordDao.getWord(word)
    }

    override suspend fun existWord(
        word: String,
        lang: String,
        length: Int,
        ratingStatus: Int
    ): Boolean {
        return wordDao.existsWord(word, lang, length, ratingStatus)
    }

    private fun mergeDictionary(
        offlineList: List<DictionaryItem>,
        syncList: List<DictionaryItem>
    ): List<DictionaryItem> {
        val offlineMap = offlineList.associateBy { it.word }
        val syncMap = syncList.associateBy { it.word }
        return (syncMap + offlineMap).values.toList()
    }
}