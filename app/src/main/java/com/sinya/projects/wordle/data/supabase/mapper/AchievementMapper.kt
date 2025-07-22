package com.sinya.projects.wordle.data.supabase.mapper

import com.sinya.projects.wordle.data.local.entity.OfflineAchievements
import com.sinya.projects.wordle.data.supabase.entity.SyncAchievements
import com.sinya.projects.wordle.data.supabase.entity.SyncDictionary
import com.sinya.projects.wordle.utils.getCurrentTime

fun OfflineAchievements.toSync(userId: String, updatedAt: String): SyncAchievements {
    return  SyncAchievements(
        achieveId = this.achieveId,
        userId = userId,
        count = this.count,
        updatedAt = updatedAt
    )
}

fun  List<OfflineAchievements>.toSyncList(userId: String): List<SyncAchievements>  {
    val updatedAt = getCurrentTime()
    return map { it.toSync(userId, updatedAt) }
}

fun SyncAchievements.toMap(): Map<String, Any> = mapOf(
    "user_id" to userId,
    "achieve_id" to achieveId,
    "count" to count,
    "updated_at" to updatedAt
)