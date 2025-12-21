package com.sinya.projects.wordle.domain.source

import com.sinya.projects.wordle.data.remote.web.LegalLinks
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
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

interface SupabaseAuthDataSource {
    val sessionStatus: Flow<SessionStatus>
    suspend fun getCurrentUser(): UserInfo?
    suspend fun signOut()
    suspend fun logIn(email: String, password: String)
    suspend fun signUp(email: String, password: String)
    suspend fun resetPassword(email: String)
    suspend fun updatePassword(password: String)
    suspend fun updateEmail(email: String)
    suspend fun parseSessionFromFragment(deepLinkUri: String): Result<UserSession>
    suspend fun importSession(session: UserSession): Result<Unit>
    suspend fun resendEmail(email: String): Result<Unit>

    suspend fun checkEmailExists(email: String): Result<Boolean>
}

class SupabaseAuthDataSourceImpl @Inject constructor(
    private val supabaseClient: SupabaseClient
) : SupabaseAuthDataSource {

    override val sessionStatus: Flow<SessionStatus> =
        supabaseClient.auth.sessionStatus

    override suspend fun getCurrentUser(): UserInfo? {
        return supabaseClient.auth.currentUserOrNull()
    }

    override suspend fun signOut() {
        supabaseClient.auth.signOut()
    }

    override suspend fun logIn(email: String, password: String) {
        supabaseClient.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun signUp(email: String, password: String) {
        supabaseClient.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun resetPassword(email: String) {
        supabaseClient.auth.resetPasswordForEmail(
            email = email,
            redirectUrl = LegalLinks.RESET_PASSWORD
        )
    }

    override suspend fun updatePassword(password: String) {
        supabaseClient.auth.updateUser {
            this.password = password
        }
    }

    override suspend fun updateEmail(email: String) {
        supabaseClient.auth.updateUser(
            redirectUrl = LegalLinks.RESET_EMAIL
        ) {
            this.email = email
        }
    }

    override suspend fun parseSessionFromFragment(deepLinkUri: String): Result<UserSession> {
        return try {
            val fragment = supabaseClient.auth.parseSessionFromFragment(deepLinkUri)
            Result.success(fragment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun importSession(session: UserSession): Result<Unit> {
        return try {
            supabaseClient.auth.importSession(session)
            supabaseClient.auth.refreshCurrentSession()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resendEmail(email: String): Result<Unit> {
        return try {
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