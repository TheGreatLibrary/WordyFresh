package com.sinya.projects.wordle.domain.repository

import android.util.Log
import com.sinya.projects.wordle.data.local.database.dao.ProfilesDao
import com.sinya.projects.wordle.data.remote.supabase.entity.Profiles
import com.sinya.projects.wordle.domain.error.InvalidSendMailException
import com.sinya.projects.wordle.domain.error.UserNotAuthenticatedException
import com.sinya.projects.wordle.domain.source.SupabaseAuthDataSource
import com.sinya.projects.wordle.domain.source.SupabaseProfileDataSource
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.user.UserInfo
import jakarta.inject.Inject

interface ProfileRepository {
    suspend fun updateNickname(nickname: String): Result<Unit>
    suspend fun updateImage(image: String): Result<Unit>
    suspend fun getEmail(): Result<String>
    suspend fun clearProfile()
    suspend fun getProfile(): Result<Profiles>
    suspend fun insertProfile(profile: Profiles): Result<Unit>

    suspend fun signUp(email: String, password: String): Result<Unit>
    suspend fun signIn(email: String, password: String): Result<UserInfo?>

    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun importSession(deepLinkUri: String): Result<Unit>
    suspend fun updatePassword(password: String): Result<Unit>
    suspend fun updateEmail(email: String): Result<Unit>

    suspend fun syncFromSupabase(): Result<Unit>
    suspend fun syncFromLocal(): Result<Unit>
    suspend fun resendEmail(email: String): Result<Unit>

    suspend fun checkEmailExists(email: String): Result<Boolean>
}

class ProfileRepositoryImpl @Inject constructor(
    private val profileDao: ProfilesDao,
    private val supabaseAuthDataSource: SupabaseAuthDataSource,
    private val supabaseProfileDataSource: SupabaseProfileDataSource
) : ProfileRepository {

    override suspend fun updateNickname(nickname: String): Result<Unit> {
        val user = supabaseAuthDataSource.getCurrentUser()
            ?: return Result.failure(UserNotAuthenticatedException())

        return try {
            supabaseProfileDataSource.updateNickname(
                nickname = nickname,
                id = user.id
            ).getOrThrow()

            profileDao.updateNickname(nickname, user.id)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateImage(image: String): Result<Unit> {
        val user = supabaseAuthDataSource.getCurrentUser()
            ?: return Result.failure(UserNotAuthenticatedException())

        return try {
            supabaseProfileDataSource.updateImagePath(
                urlPath = image,
                id = user.id
            ).getOrThrow()

            profileDao.updateImageProfile(image, user.id)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getEmail(): Result<String> {
        val user = supabaseAuthDataSource.getCurrentUser()
            ?: return Result.failure(UserNotAuthenticatedException())

        return try {
            Result.success(user.email ?: "Данные не найдены")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun clearProfile() {
        profileDao.clearAll()
    }

    override suspend fun getProfile(): Result<Profiles> {
        Log.d("ЫЫЫ", "начинаю получать профиль")
        val user = supabaseAuthDataSource.getCurrentUser()
            ?: return Result.failure(UserNotAuthenticatedException())

        Log.d("ЫЫЫ", "что-то получил $user")

        return try {
            val profile = supabaseProfileDataSource.fetchProfile(user.id)
            Log.d("ЫЫЫ", "профиль $profile")

            if (profile != null) Result.success(profile)
            else Result.failure(UserNotAuthenticatedException())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun insertProfile(profile: Profiles): Result<Unit> {
        return try {
            supabaseProfileDataSource.updateProfile(profile)
            profileDao.insertProfile(profile)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    override suspend fun signUp(email: String, password: String): Result<Unit> {
        return try {
            supabaseAuthDataSource.signUp(email, password)

//            val user = supabaseAuthDataSource.getCurrentUser()
//                ?: return Result.failure(UserNotAuthenticatedException())
//
//            val profile = supabaseProfileDataSource.fetchProfile(user.id)
//                ?: return Result.failure(UserNotAuthenticatedException())
//
//            profileDao.insertProfile(profile)

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("LoginUseCase", "Error during login", e)
            Result.failure(e)
        }
    }

    override suspend fun signIn(email: String, password: String): Result<UserInfo?> {
        return try {
            supabaseAuthDataSource.logIn(email, password)

            val user = supabaseAuthDataSource.getCurrentUser()
                ?: return Result.failure(UserNotAuthenticatedException())

//            val profile = supabaseProfileDataSource.fetchProfile(user.id)
//                ?: return Result.failure(UserNotAuthenticatedException())

//            profileDao.insertProfile(profile)

            Result.success(user)
        } catch (e: Exception) {
            Log.e("LoginUseCase", "Error during login", e)
            Result.failure(e)
        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            supabaseAuthDataSource.resetPassword(email)
                ?: return Result.failure(InvalidSendMailException())

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ResetPasswordUseCase", "Error during reset password", e)
            Result.failure(e)
        }
    }

    override suspend fun importSession(deepLinkUri: String): Result<Unit> {
        return try {
            val fragment = deepLinkUri.substringAfter("#", "")
            if (fragment.isEmpty()) {
                return Result.failure(Exception("Invalid deep link"))
            }

            supabaseAuthDataSource.parseSessionFromFragment(fragment).fold(
                onSuccess = { session ->
                    supabaseAuthDataSource.importSession(session)
                },
                onFailure = { e ->
                    Result.failure(e)
                }
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ResetPasswordUseCase", "Error during import session", e)
            Result.failure(e)
        }
    }

    override suspend fun updatePassword(password: String): Result<Unit> {
        return try {
//            supabaseAuthDataSource.getCurrentUser()
//            ?: return Result.failure(UserNotAuthenticatedException())

            supabaseAuthDataSource.updatePassword(password)

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UpdatePasswordUseCase", "Error during update password", e)
            Result.failure(e)
        }
    }

    override suspend fun updateEmail(email: String): Result<Unit> {
        return try {
            supabaseAuthDataSource.getCurrentUser()
                ?: return Result.failure(UserNotAuthenticatedException())

            supabaseAuthDataSource.updateEmail(email)

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UpdatePasswordUseCase", "Error during update password", e)
            Result.failure(e)
        }
    }

    override suspend fun syncFromSupabase(): Result<Unit> {
        return try {
            val user = supabaseAuthDataSource.getCurrentUser()
                ?: return Result.failure(UserNotAuthenticatedException())

            supabaseProfileDataSource.syncFromSupabase(user.id)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncFromLocal(): Result<Unit> {
        return try {
            val user = supabaseAuthDataSource.getCurrentUser()
                ?: return Result.failure(UserNotAuthenticatedException())

            supabaseProfileDataSource.syncToSupabase(user.id)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resendEmail(email: String): Result<Unit> {
        return supabaseAuthDataSource.resendEmail(email).fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { e ->
                Log.e("ResendEmailUseCase", "Error during resend email", e)
                Result.failure(e)
            }
        )
    }

    override suspend fun checkEmailExists(email: String): Result<Boolean> {
        return supabaseAuthDataSource.checkEmailExists(email)
    }
}