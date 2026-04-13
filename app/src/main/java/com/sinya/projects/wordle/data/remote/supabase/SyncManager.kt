package com.sinya.projects.wordle.data.remote.supabase

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.sinya.projects.wordle.domain.useCase.SyncDataUseCase
import io.github.jan.supabase.auth.user.UserInfo
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

@Singleton
class SyncManager @Inject constructor(
    private val sessionManager: SessionManager,
    private val syncDataUseCase: SyncDataUseCase
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val syncMutex = Mutex()
    private var lastSyncTime = 0L

    fun initialize() {
        observeSession()
        observeLifecycle()
    }

    private fun observeSession() = scope.launch {
        sessionManager.userInfo
            .scan(Pair<UserInfo?, UserInfo?>(null, null)) { acc, new -> Pair(acc.second, new) }
            .filter { (prev, curr) -> prev == null && curr != null }  // null → authed
            .collect {
                delay(1500)
                performSync()
            }
    }

    private fun observeLifecycle() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                if (sessionManager.currentUserId != null) {
                    scope.launch {
                        delay(500)
                        performSync()
                    }
                }
            }
        })
    }

    private suspend fun performSync() {
        val now = System.currentTimeMillis()
        if (now - lastSyncTime < 5 * 60 * 1000) return
        if (!syncMutex.tryLock()) return
        try {
            lastSyncTime = now
            syncDataUseCase()
        } finally {
            syncMutex.unlock()
        }
    }
}