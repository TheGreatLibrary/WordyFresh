package com.sinya.projects.wordle.domain.source

import android.util.Log
import com.sinya.projects.wordle.data.local.database.dao.OfflineDictionaryDao
import com.sinya.projects.wordle.data.local.database.dao.SyncDictionaryDao
import com.sinya.projects.wordle.data.remote.supabase.entity.SyncDictionary
import com.sinya.projects.wordle.data.remote.supabase.mapper.toSyncList
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

interface SupabaseDictionaryDataSource {
    suspend fun fetchDictionary(userId: String): Result<List<SyncDictionary>>
    suspend fun upsertDictionary(dictionary: List<SyncDictionary>): Result<Unit>
    suspend fun clearAllDictionary(userId: String): Result<Unit>
    suspend fun syncToSupabase(userId: String): Result<Unit>
    suspend fun syncFromSupabase(userId: String): Result<Unit>
}

class SupabaseDictionaryDataSourceImpl @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val syncDictionaryDao: SyncDictionaryDao,
    private val offlineDictionaryDao: OfflineDictionaryDao
) : SupabaseDictionaryDataSource {

    override suspend fun fetchDictionary(userId: String): Result<List<SyncDictionary>> {
        return withContext(Dispatchers.IO) {
            try {
                val dictionary = supabaseClient
                    .from("sync_dictionary")
                    .select { filter { eq("user_id", userId) } }
                    .decodeList<SyncDictionary>()

                Result.success(dictionary)
            } catch (e: Exception) {
                Log.e("SupabaseDictionaryDataSource", "Error fetching dictionary", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun upsertDictionary(dictionary: List<SyncDictionary>): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val json = Json { encodeDefaults = true }

                val jsonList = dictionary.map {
                    json.parseToJsonElement(json.encodeToString(it)).jsonObject
                }

                supabaseClient.from("sync_dictionary").upsert(jsonList) {
                    onConflict = "user_id,word_id"
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("SupabaseDictionaryDataSource", "Error upserting dictionary", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun clearAllDictionary(userId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                supabaseClient.from("sync_dictionary")
                    .delete { filter { eq("user_id", userId) } }
                syncFromSupabase(userId).getOrThrow()
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("SupabaseDictionaryDataSource", "Error clearing dictionary", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun syncToSupabase(userId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val offline = offlineDictionaryDao
                    .getDictionary()
                    .toSyncList(userId)

                if (offline.isNotEmpty()) {
                    upsertDictionary(offline).getOrThrow()
                }

                offlineDictionaryDao.clearAll()

                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("SupabaseDictionaryDataSource", "Error syncing to Supabase", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun syncFromSupabase(userId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val remote = fetchDictionary(userId).getOrThrow()
                syncDictionaryDao.clearAll()
                if (remote.isNotEmpty()) {
                    syncDictionaryDao.insertList(remote)
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("SupabaseDictionaryDataSource", "Error syncing from Supabase", e)
                Result.failure(e)
            }
        }
    }
}