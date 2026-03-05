package com.sinya.projects.wordle.data.remote.supabase

import android.net.Uri
import android.util.Log
import com.sinya.projects.wordle.domain.repository.AvatarRepository
import com.sinya.projects.wordle.domain.source.SupabaseAuthDataSource
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserInfo
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Singleton
class SessionManager @Inject constructor(
    private val authDataSource: SupabaseAuthDataSource,
    private val avatarRepository: AvatarRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo: StateFlow<UserInfo?> = _userInfo.asStateFlow()

    private val _avatar = MutableStateFlow<Uri?>(null)
    val avatar: StateFlow<Uri?> = _avatar.asStateFlow()

    val currentUserId: String?
        get() = _userInfo.value?.id

    init {
        observeAuth()
    }

    private fun observeAuth() = scope.launch {
        authDataSource.sessionStatusFlow().collect { status ->
            Log.d("SessionManager", "status: $status")
            when (status) {
                is SessionStatus.Authenticated -> {
                    val user = status.session.user
                    Log.d("SessionManager", "user: $user")
                    _userInfo.value =
                        user?.let { UserInfo(aud = it.aud, id = it.id, email = it.email ?: "") }
                    user?.id?.let { loadAvatar(it) }
                }

                is SessionStatus.NotAuthenticated -> {
                    _userInfo.value = null
                    _avatar.value = null
                }

                else -> {}
            }
        }
    }

    private suspend fun loadAvatar(userId: String) {
        _avatar.value = avatarRepository.downloadAvatar(userId).getOrNull()
    }

    suspend fun uploadAvatar(uri: Uri): Result<Unit> {
        val userId = currentUserId ?: return Result.failure(Exception("Not authenticated"))
        _avatar.value = uri
        return avatarRepository.uploadAvatar(userId, uri)
            .map { }
            .onFailure { _avatar.value = null }
    }
}