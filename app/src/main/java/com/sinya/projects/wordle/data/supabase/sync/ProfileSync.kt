package com.sinya.projects.wordle.data.supabase.sync

import android.content.Context
import android.util.Log
import com.sinya.projects.wordle.WordyApplication
import com.sinya.projects.wordle.data.supabase.Syncable
import com.sinya.projects.wordle.data.supabase.entity.Profiles
import io.github.jan.supabase.postgrest.from

object ProfileSync : Syncable {
    override suspend fun toSupabase(context: Context, userId: String) {
        val db = WordyApplication.database
        val profile = db.profilesDao().getProfileById(userId)

        try {
            WordyApplication.supabaseClient
                .from("profiles")
                .update(profile)

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
                db.profilesDao().updateProfile(updated)
            }

        } catch (e: Exception) {
            Log.e("Profile", "Ошибка загрузки: ${e.localizedMessage}")
        }
    }
}
