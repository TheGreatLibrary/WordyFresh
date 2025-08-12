package com.sinya.projects.wordle.data.remote.supabase.sync

import android.content.Context
import android.util.Log
import com.sinya.projects.wordle.WordyApplication
import com.sinya.projects.wordle.data.remote.supabase.Syncable
import com.sinya.projects.wordle.data.remote.supabase.entity.Profiles
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

object ProfileSync : Syncable {
    override suspend fun toSupabase(context: Context, userId: String) {
        val db = WordyApplication.database
        val profile = db.profilesDao().getProfileById(userId)

        try {
            val json = Json { encodeDefaults = true }
            val payload = json.encodeToJsonElement(profile).jsonObject

            WordyApplication.supabaseClient
                .from("profiles")
                .upsert(payload)
        } catch (e: Exception) {
            Log.e("Profile", "Ошибка: ${e.localizedMessage}")
        }
    }

    override suspend fun fromSupabase(context: Context, userId: String) {
        val db = WordyApplication.database

        try {
            val updated = WordyApplication.supabaseClient
                .from("profiles")
                .select {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingleOrNull<Profiles>()

            if (updated != null) {
                db.profilesDao().insertProfile(updated)
            }

        } catch (e: Exception) {
            Log.e("Profile", "Ошибка загрузки: ${e.localizedMessage}")
        }
    }
}
