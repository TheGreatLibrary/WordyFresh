package com.sinya.projects.wordle.domain.repository

import android.util.Log
import com.sinya.projects.wordle.data.local.database.dao.OfflineDictionaryDao
import com.sinya.projects.wordle.data.local.database.dao.SyncDictionaryDao
import com.sinya.projects.wordle.data.local.database.dao.WordDao
import com.sinya.projects.wordle.data.local.database.entity.Words
import com.sinya.projects.wordle.domain.error.DefinitionNotFoundException
import com.sinya.projects.wordle.domain.error.NoInternetException
import com.sinya.projects.wordle.domain.error.UserNotAuthenticatedException
import com.sinya.projects.wordle.domain.error.WordNotFoundException
import com.sinya.projects.wordle.domain.model.DictionaryItem
import com.sinya.projects.wordle.domain.source.DictionaryDataSource
import com.sinya.projects.wordle.domain.source.SupabaseAuthDataSource
import com.sinya.projects.wordle.domain.source.SupabaseDictionaryDataSource
import com.sinya.projects.wordle.domain.source.WiktionaryDataSource
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

interface DictionaryRepository {

    // DictionaryScreen
    fun getAllWords(): Flow<List<DictionaryItem>>
    suspend fun clearAllDictionary(): Result<Unit>

    // ProfileScreen
    suspend fun clearLocal(): Result<Unit>

    // GameScreen
    suspend fun existWord(word: String, lang: String, length: Int, ratingStatus: Int): Result<Unit>
    suspend fun getWord(word: String): Result<Words>
    suspend fun getWordRating(word: String): Result<Boolean>
    suspend fun saveDefinition(wordId: Int, definition: String)
    suspend fun getRandomWord(length: Int, lang: String, ratingStatus: Boolean): Result<String>

    // SyncViewModel
    suspend fun syncFromSupabase(): Result<Unit>
    suspend fun syncFromLocal(): Result<Unit>
}

class DictionaryRepositoryImpl @Inject constructor(
    private val offlineDictionaryDao: OfflineDictionaryDao,
    private val syncDictionaryDao: SyncDictionaryDao,
    private val wordDao: WordDao,
    private val dictionaryDataSource: WiktionaryDataSource,
    private val supabaseAuthDataSource: SupabaseAuthDataSource,
    private val supabaseDictionaryDataSource: SupabaseDictionaryDataSource
) : DictionaryRepository {

    // DictionaryScreen

    override suspend fun clearAllDictionary(): Result<Unit> {
        return try {
            offlineDictionaryDao.clearAll()

            val user = supabaseAuthDataSource.getCurrentUser()
            if (user != null) supabaseDictionaryDataSource.clearAllDictionary(user.id)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getAllWords(): Flow<List<DictionaryItem>> {
        return offlineDictionaryDao.getAllWords().catch { emit(emptyList()) }
    }

    // ProfileScreen

    override suspend fun clearLocal(): Result<Unit> {
        return try {
            offlineDictionaryDao.clearAll()
            syncDictionaryDao.clearAll()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // GameScreen

    override suspend fun saveDefinition(wordId: Int, definition: String) {
        offlineDictionaryDao.insertOrUpdateDescription(wordId, definition)
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

    override suspend fun getWord(word: String): Result<Words> {
        return try {
            val wordObj = wordDao.getWord(word)
                ?: return Result.failure(WordNotFoundException())

            Result.success(wordObj)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun existWord(
        word: String,
        lang: String,
        length: Int,
        ratingStatus: Int
    ): Result<Unit> {
        return try {
            val result = wordDao.existsWord(word, lang, length, ratingStatus)

            if (result) Result.success(Unit)
            else Result.failure(WordNotFoundException())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // SyncViewModel

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
}