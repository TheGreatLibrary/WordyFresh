package com.sinya.projects.wordle.data.remote.supabase

import android.content.Context
import com.sinya.projects.wordle.WordyApplication
import com.sinya.projects.wordle.data.remote.supabase.sync.AchievementSync
import com.sinya.projects.wordle.data.remote.supabase.sync.DictionarySync
import com.sinya.projects.wordle.data.remote.supabase.sync.ProfileSync
import com.sinya.projects.wordle.data.remote.supabase.sync.StatisticSync
import io.github.jan.supabase.auth.auth

object SyncManager {
    private val syncables: List<Syncable> = listOf(
        ProfileSync,
        DictionarySync,
        StatisticSync,
        AchievementSync
    )

    suspend fun syncAllToSupabase(context: Context, userId: String) {
        for (syncable in syncables) {
            syncable.toSupabase(context, userId)
        }
    }

    suspend fun syncAllToLocal(context: Context, userId: String) {
        for (syncable in syncables) {
            syncable.fromSupabase(context, userId)
        }
    }
}