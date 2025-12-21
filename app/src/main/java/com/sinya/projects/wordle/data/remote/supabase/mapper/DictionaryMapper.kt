package com.sinya.projects.wordle.data.remote.supabase.mapper

import com.sinya.projects.wordle.data.local.database.entity.OfflineDictionary
import com.sinya.projects.wordle.data.remote.supabase.entity.SyncDictionary
import com.sinya.projects.wordle.data.remote.supabase.entity.SyncStatistic
import com.sinya.projects.wordle.utils.getCurrentTime

fun OfflineDictionary.toSync(userId: String, updatedAt: String): SyncDictionary {
    return SyncDictionary(
        userId = userId,
        wordId = this.wordId,
        description = this.description,
        updatedAt = updatedAt
    )
}

fun List<OfflineDictionary>.toSyncList(userId: String): List<SyncDictionary> {
    val updatedAt = getCurrentTime()
    return map { it.toSync(userId, updatedAt) }
}

fun SyncDictionary.toMap(): Map<String, Any> = mapOf(
    "user_id" to userId,
    "word_id" to wordId,
    "description" to description,
    "updated_at" to updatedAt
)