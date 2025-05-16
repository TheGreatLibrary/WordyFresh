package com.sinya.projects.wordle.data.remote.supabase

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.sinya.projects.wordle.data.local.database.AppDatabase
import com.sinya.projects.wordle.domain.model.toSyncAchievements
import com.sinya.projects.wordle.domain.model.toSyncDictionary
import com.sinya.projects.wordle.domain.model.toSyncStatistic
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object SupabaseSyncManager {

    suspend fun syncAllToLocal(context: Context, userId: String) {
        if (!isInternetAvailable(context)) return

        val db = AppDatabase.getInstance(context)

        try {
            val user = SupabaseService.fetchProfile(userId)
            val dictionary = SupabaseService.fetchSyncDictionary(userId)
            val statistic = SupabaseService.fetchSyncStatistics(userId)
            val achievements = SupabaseService.fetchSyncAchievements(userId)

            db.profilesDao().insertProfile(user)
            db.syncDictionaryDao().insertList(dictionary)
            db.syncStatisticDao().insertList(statistic)
            db.syncAchievementsDao().updateAchievementsList(achievements)

            Log.d("SupabaseSyncManager", "Синхронизация завершена")
        } catch (e: Exception) {
            Log.e("Пиздец", "Ошибка: ${e.message}", e)
        }
    }

    suspend fun syncAllToSupabase(context: Context) {
        if (!isInternetAvailable(context)) return

        val db = AppDatabase.getInstance(context)

        try {
            val supabase = SupabaseClientHolder.client
            val userId = supabase.auth.currentUserOrNull()?.id
            if (userId == null) {
                Log.w("SupabaseSync", "User ID is null — возможно, пользователь не залогинен")
                return
            }

            val user = db.profilesDao().getProfileById(userId)
            val dictionary = db.offlineDictionaryDao().getDictionary()
            val statistic = db.offlineStatisticDao().getAllStatistic()
            val achievements = db.offlineAchievementsDao().getAchievements()

            val profileUploaded = SupabaseService.upsertProfile(user)
            val dictionaryUploaded =
                SupabaseService.upsertDictionaryList(dictionary.toSyncDictionary(userId))
            val achievementsUploaded =
                SupabaseService.upsertAchievementList(achievements.toSyncAchievements(userId))

            val json = Json { encodeDefaults = true }

            val statisticsJsonList = statistic.toSyncStatistic(userId).map {
                val jsonString = json.encodeToString(it)
                json.parseToJsonElement(jsonString).jsonObject
            }

            supabase.from("sync_statistic").upsert(statisticsJsonList) {
                onConflict = "user_id,mode_id"
            }
//            val statisticUploaded = SupabaseService.upsertStatisticList(statisticsJsonList)

            if (profileUploaded) {
                Log.d("SupabaseSyncManagerProfile", "Синхронизация профиля завершена")
            }
            if (dictionaryUploaded) {
                db.offlineDictionaryDao().clear()
                Log.d(
                    "SupabaseSyncManagerDictionary",
                    "Синхронизация завершена и локальные данные удалены"
                )
            }
            if (achievementsUploaded) {
                db.offlineStatisticDao().clear()
                Log.d(
                    "SupabaseSyncManagerAchievements",
                    "Синхронизация завершена и локальные данные удалены"
                )
            }
//            if (statisticUploaded) {
                db.offlineAchievementsDao().clear()
//                Log.d(
//                    "SupabaseSyncManagerStatistic",
//                    "Синхронизация завершена и локальные данные удалены"
//                )
//            }

            Log.d("SupabaseSyncManagerStatistic", "я даун")
            // Здесь безопасно снова синхронизировать с сервера

        } catch (e: Exception) {
            Log.e("SupabaseSyncManager", "Ошибка: ${e.message}", e)
        }
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo?.isConnected == true
    }

    fun getCurrentIsoTimestamp(): String {
        return OffsetDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }
}