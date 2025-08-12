package com.sinya.projects.wordle.data.remote.supabase.sync

import android.content.Context
import android.util.Log
import com.sinya.projects.wordle.WordyApplication
import com.sinya.projects.wordle.data.remote.supabase.Syncable
import com.sinya.projects.wordle.data.local.entity.OfflineAchievements
import com.sinya.projects.wordle.data.remote.supabase.entity.SyncAchievements
import com.sinya.projects.wordle.utils.getCurrentTime
import io.github.jan.supabase.postgrest.from

object AchievementSync : Syncable {

    override suspend fun toSupabase(context: Context, userId: String) {
        val db = WordyApplication.database
        val supabase = WordyApplication.supabaseClient

        try {
            // 1. Получаем актуальные данные из Supabase
            val remote = supabase
                .from("sync_achievements")
                .select {
                    filter { eq("user_id", userId) }
                }
                .decodeList<SyncAchievements>()

            // 2. Получаем offline-достижения
            val offline = db.offlineAchievementsDao().getAchievements()

            // 3. Объединяем
            val merged = mergeAchievements(remote, offline, userId)

            // 4. Отправляем
            supabase
                .from("sync_achievements")
                .upsert(merged)

            // 5. Удаляем offline, если всё прошло успешно
            db.offlineAchievementsDao().clearAll()

        } catch (e: Exception) {
            Log.e("AchievementSync", "Ошибка отправки: ${e.localizedMessage}")
        }
    }

    override suspend fun fromSupabase(context: Context, userId: String) {
        val db = WordyApplication.database

        try {
            val remote = WordyApplication.supabaseClient
                .from("sync_achievements")
                .select {
                    filter { eq("user_id", userId) }
                }
                .decodeList<SyncAchievements>()

            val local = db.syncAchievementsDao().getAll()
            val localMap = local.associateBy { it.achieveId }

            val filtered = remote.filter { remoteItem ->
                val localItem = localMap[remoteItem.achieveId]
                localItem == null || remoteItem.updatedAt > localItem.updatedAt
            }

            db.syncAchievementsDao().insertOrReplace(filtered)

        } catch (e: Exception) {
            Log.e("AchievementSync2", "Ошибка загрузки: ${e.localizedMessage}")
        }
    }

    private fun mergeAchievements(
        remote: List<SyncAchievements>,
        offline: List<OfflineAchievements>,
        userId: String
    ): List<SyncAchievements> {
        val updatedAt = getCurrentTime()
        val remoteMap = remote.associateBy { it.achieveId }.toMutableMap()

        for (local in offline) {
            val remoteItem = remoteMap[local.achieveId]

            val merged = remoteItem?.copy(
                userId = userId,
                count = remoteItem.count + local.count,
                updatedAt = updatedAt
            ) ?: SyncAchievements(
                userId = userId,
                achieveId = local.achieveId,
                count = local.count,
                updatedAt = updatedAt
            )

            remoteMap[local.achieveId] = merged
        }

        return remoteMap.values.toList()
    }
}