package com.sinya.projects.wordle.data.remote.supabase

import android.content.Context

interface Syncable {
    suspend fun fromSupabase(context: Context, userId: String)

    suspend fun toSupabase(context: Context, userId: String)
}
