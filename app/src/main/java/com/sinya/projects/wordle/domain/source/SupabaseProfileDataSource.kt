package com.sinya.projects.wordle.domain.source

import android.util.Log
import com.sinya.projects.wordle.data.local.database.dao.ProfilesDao
import com.sinya.projects.wordle.data.remote.supabase.entity.Profiles
import com.sinya.projects.wordle.domain.checker.NetworkChecker
import com.sinya.projects.wordle.domain.error.NoInternetException
import com.sinya.projects.wordle.domain.error.UserNotAuthenticatedException
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.selectSingleValueAsFlow
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

interface SupabaseProfileDataSource {
    suspend fun updateProfile(profile: Profiles): Result<Unit>
    suspend fun updateImagePath(urlPath: String, id: String): Result<Unit>
    suspend fun updateNickname(nickname: String, id: String): Result<Unit>
    suspend fun syncToSupabase(userId: String): Result<Unit>
    suspend fun syncFromSupabase(userId: String): Result<Profiles?>
    suspend fun observeProfile(userId: String): Flow<Profiles?>
}

class SupabaseProfileDataSourceImpl @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val profilesDao: ProfilesDao,
    private val networkChecker: NetworkChecker
) : SupabaseProfileDataSource {

    @OptIn(SupabaseExperimental::class)
    override suspend fun observeProfile(userId: String): Flow<Profiles?> =
        supabaseClient
            .from("profiles")
            .selectSingleValueAsFlow(Profiles::id) {
                eq("id", userId)
            }

    override suspend fun updateProfile(profile: Profiles): Result<Unit> {
        return withContext(Dispatchers.IO) {
            if (!networkChecker.isInternetAvailable()) return@withContext Result.failure(NoInternetException())

            try {
                val json = Json { encodeDefaults = true }
                val payload = json.encodeToJsonElement(profile).jsonObject

                supabaseClient.from("profiles").upsert(payload)
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("SupabaseProfileDataSource", "Error updating profile", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun updateImagePath(urlPath: String, id: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                if (!networkChecker.isInternetAvailable()) return@withContext Result.failure(NoInternetException())

                supabaseClient.from("profiles").update({ Profiles::avatarUrl setTo urlPath }) {
                    filter { Profiles::id eq id }
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("SupabaseProfileDataSource", "Error updating profile", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun updateNickname(nickname: String, id: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                if (!networkChecker.isInternetAvailable()) return@withContext Result.failure(NoInternetException())

                supabaseClient.from("profiles").update({ Profiles::nickname setTo nickname }) {
                    filter { Profiles::id eq id }
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("SupabaseProfileDataSource", "Error updating profile", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun syncToSupabase(userId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                if (!networkChecker.isInternetAvailable()) return@withContext Result.failure(NoInternetException())

                val profile = profilesDao.getProfileById(userId)
                    ?: return@withContext Result.failure(UserNotAuthenticatedException())

                val json = Json { encodeDefaults = true }
                val payload = json.encodeToJsonElement(profile).jsonObject

                supabaseClient.from("profiles").upsert(payload)
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("SupabaseProfileDataSource", "Error syncing to Supabase", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun syncFromSupabase(userId: String): Result<Profiles?> {
        return withContext(Dispatchers.IO) {
            try {
                if (!networkChecker.isInternetAvailable()) return@withContext Result.failure(NoInternetException())

                val profile = supabaseClient
                    .from("profiles")
                    .select { filter { eq("id", userId) } }
                    .decodeSingleOrNull<Profiles>()

                if (profile != null) {
                    profilesDao.insertProfile(profile)
                }

                Result.success(profile)
            } catch (e: Exception) {
                Log.e("SupabaseProfileDataSource", "Error syncing from Supabase", e)
                Result.failure(e)
            }
        }
    }
}