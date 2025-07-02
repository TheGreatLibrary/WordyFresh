package com.sinya.projects.wordle.data.remote.supabase

import android.util.Log
import com.sinya.projects.wordle.WordyApplication
import com.sinya.projects.wordle.domain.model.entity.Profiles
import com.sinya.projects.wordle.domain.model.entity.SyncDictionary
import com.sinya.projects.wordle.domain.model.entity.SyncStatistic
import com.sinya.projects.wordle.domain.model.supabase.SyncAchievements
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

object SupabaseService {

    suspend fun insertNewProfile(profile: Profiles) {
        val supabase = WordyApplication.supabaseClient
        supabase.from("profiles").insert(profile)
    }

    /**
     * Получение последних синхронизированных данных из Supabase в БД.
     *
     * Данные берутся по id пользователя, который сейчас находится актуальным на телефоне.
     */
    suspend fun fetchProfile(userId: String): Profiles? {
        val supabase = WordyApplication.supabaseClient
        return supabase
            .from("profiles")
            .select {
                filter { eq("id", userId) }
            }
            .decodeSingleOrNull<Profiles>()
    }

    suspend fun fetchSyncDictionary(userId: String): List<SyncDictionary> {
        val supabase = WordyApplication.supabaseClient
        return supabase
            .from("sync_dictionary")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<SyncDictionary>()
    }

    suspend fun fetchSyncStatistics(userId: String): List<SyncStatistic> {
        val supabase = WordyApplication.supabaseClient
        return supabase
            .from("sync_statistic")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<SyncStatistic>()
    }

    suspend fun fetchSyncAchievements(userId: String): List<SyncAchievements> {
        val supabase = WordyApplication.supabaseClient
        return supabase
            .from("sync_achievements")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<SyncAchievements>()
    }


    /**
     * Отправка сохраненных данных в таблицах из SQLite в Supabase
     *
     * Данные отправляются и вставляются на id таком-то
     */
    suspend fun upsertProfile(user: Profiles) : Boolean {
        val supabase = WordyApplication.supabaseClient
        return try {
            supabase.from("profiles").upsert(user)
            true
        } catch (e: Exception) {
            Log.e("SupabaseService", "Ошибка при отправке профиля: ${e.message}", e)
            false
        }
    }

    suspend fun upsertDictionaryList(dictionary: List<SyncDictionary>) : Boolean {
        val supabase = WordyApplication.supabaseClient
        return try {
            supabase.from("sync_dictionary").upsert(dictionary)
            true
        } catch (e: Exception) {
            Log.e("SupabaseService", "Ошибка при отправке словаря: ${e.message}", e)
            false
        }
    }

    suspend fun upsertStatisticList(statistics: List<SyncStatistic>) : Boolean {
        val supabase = WordyApplication.supabaseClient
        return try {
            supabase.from("sync_statistic").upsert(statistics) {
                onConflict= "user_id,mode_id" }
            true
        } catch (e: Exception) {
            Log.e("SupabaseService", "Ошибка при отправке статистики: ${e.message}", e)
            false
        }
    }

    suspend fun upsertAchievementList(achievements: List<SyncAchievements>) : Boolean {
        val supabase = WordyApplication.supabaseClient
        return try {
            supabase.from("sync_achievements").upsert(achievements)
            true
        } catch (e: Exception) {
            Log.e("SupabaseService", "Ошибка при отправке достижений: ${e.message}", e)
            false
        }
    }
}