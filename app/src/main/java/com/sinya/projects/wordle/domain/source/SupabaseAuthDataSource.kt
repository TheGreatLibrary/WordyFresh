package com.sinya.projects.wordle.domain.source

import com.sinya.projects.wordle.data.remote.web.LegalLinks
import com.sinya.projects.wordle.domain.checker.NetworkChecker
import com.sinya.projects.wordle.domain.error.NoInternetException
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.parseSessionFromFragment
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.postgrest.postgrest
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

interface SupabaseAuthDataSource {
    fun authUserIdFlow(): Flow<String?>
    fun sessionStatusFlow(): Flow<SessionStatus>
    fun getCurrentUser(): UserInfo?
    suspend fun signOut()
    suspend fun logIn(email: String, password: String)
    suspend fun signUp(email: String, password: String)
    suspend fun resetPassword(email: String)
    suspend fun updatePassword(password: String)
    suspend fun updateEmail(email: String)
    fun parseSessionFromFragment(deepLinkUri: String): Result<UserSession>
    suspend fun importSession(session: UserSession): Result<Unit>
    suspend fun resendEmail(email: String): Result<Unit>
    suspend fun checkEmailExists(email: String): Result<Boolean>
}

class SupabaseAuthDataSourceImpl @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val networkChecker: NetworkChecker
) : SupabaseAuthDataSource {

    override fun getCurrentUser(): UserInfo? {
        return supabaseClient.auth.currentUserOrNull()
    }

    override fun authUserIdFlow(): Flow<String?> {
        return supabaseClient.auth.sessionStatus.map { (it as? SessionStatus.Authenticated)?.session?.user?.id }
    }

    override fun sessionStatusFlow(): Flow<SessionStatus> {
        return supabaseClient.auth.sessionStatus
    }

    override suspend fun signOut() {
        if (!networkChecker.isInternetAvailable()) throw NoInternetException()

        supabaseClient.auth.signOut()
    }

    override suspend fun logIn(email: String, password: String) {
        if (!networkChecker.isInternetAvailable()) throw NoInternetException()

        supabaseClient.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun signUp(email: String, password: String) {
        if (!networkChecker.isInternetAvailable()) throw NoInternetException()

        supabaseClient.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun resetPassword(email: String) {
        if (!networkChecker.isInternetAvailable()) throw NoInternetException()

        supabaseClient.auth.resetPasswordForEmail(
            email = email,
            redirectUrl = LegalLinks.RESET_PASSWORD
        )
    }

    override suspend fun updatePassword(password: String) {
        if (!networkChecker.isInternetAvailable()) throw NoInternetException()

        supabaseClient.auth.updateUser {
            this.password = password
        }
    }

    override suspend fun updateEmail(email: String) {
        if (!networkChecker.isInternetAvailable()) throw NoInternetException()

        supabaseClient.auth.updateUser(
            redirectUrl = LegalLinks.RESET_EMAIL
        ) {
            this.email = email
        }
    }

    override fun parseSessionFromFragment(deepLinkUri: String): Result<UserSession> {
        return try {
            val fragment = supabaseClient.auth.parseSessionFromFragment(deepLinkUri)
            Result.success(fragment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun importSession(session: UserSession): Result<Unit> {
        return try {
            if (!networkChecker.isInternetAvailable()) return Result.failure(NoInternetException())

            supabaseClient.auth.importSession(session)
            supabaseClient.auth.refreshCurrentSession()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resendEmail(email: String): Result<Unit> {
        return try {
            if (!networkChecker.isInternetAvailable()) return Result.failure(NoInternetException())

            supabaseClient.auth.resendEmail(
                OtpType.Email.SIGNUP,
                email = email
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkEmailExists(email: String): Result<Boolean> {
        return try {
            if (!networkChecker.isInternetAvailable()) throw NoInternetException()

            val result = supabaseClient.postgrest.rpc(
                function = "check_email_exists",
                parameters = buildJsonObject {
                    put("email_to_check", email)
                }
                ).decodeAs<Boolean>()

            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}