package com.sinya.projects.wordle.data.supabase.sync

import android.content.Context
import android.util.Log
import com.sinya.projects.wordle.WordyApplication
import com.sinya.projects.wordle.data.supabase.Syncable
import com.sinya.projects.wordle.data.supabase.mapper.toSyncList
import com.sinya.projects.wordle.data.supabase.entity.SyncDictionary
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

object DictionarySync : Syncable {
    override suspend fun toSupabase(context: Context, userId: String) {
        val db = WordyApplication.database
        val offline = db.offlineDictionaryDao().getDictionary()
        val payload = offline.toSyncList(userId)

        try {
            val json = Json { encodeDefaults = true }

            val jsonString = payload.map {
                val jsonString = json.encodeToString(it)
                json.parseToJsonElement(jsonString).jsonObject
            }


            WordyApplication.supabaseClient
                .from("sync_dictionary")
                .upsert(jsonString)

            db.offlineDictionaryDao().clearAll()
        } catch (e: Exception) {
            Log.e("DictionarySync1", "Ошибка: ${e.localizedMessage}")
        }
    }

    override suspend fun fromSupabase(context: Context, userId: String) {
        val db = WordyApplication.database

        try {
            val remoteItems = WordyApplication.supabaseClient
                .from("sync_dictionary")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<SyncDictionary>()

            db.syncDictionaryDao().insertList(remoteItems)

        } catch (e: Exception) {
            Log.e("DictionarySync2", "Ошибка загрузки: ${e.localizedMessage}")
        }
    }
}
