package com.sinya.projects.wordle.data.remote.supabase

import android.util.Log
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
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

    private var lastSyncedUserId: String? = null
    private val isAppInForeground = MutableStateFlow(false)

    private val syncTicker = flow {
        while (true) {
            emit(System.currentTimeMillis())
            delay(1 * 60 * 1000)
        }
    }

    fun initialize() {
        observeLifecycle()
        observeSyncTrigger()
    }

    private fun observeSyncTrigger() = scope.launch {
        combine(
            isAppInForeground,
            sessionManager.userInfo,
            syncTicker
        ) { inForeground, userInfo, timestamp ->
            Triple(inForeground, userInfo, timestamp)
        }
            .distinctUntilChanged { old, new ->
                old.first == new.first && old.second?.id == new.second?.id && old.third == new.third
            }
            .collect { (inForeground, userInfo, _) ->
                if (userInfo == null) {
                    Log.d(
                        "SyncV",
                        "Пользователь разлогинился. Полностью сбрасываем таймеры сессии."
                    )
                    lastSyncTime = 0L
                    lastSyncedUserId = null
                    return@collect
                }

                if (!inForeground) return@collect

                if (userInfo.id != lastSyncedUserId) {
                    Log.d(
                        "SyncV",
                        "Новая сессия для пользователя (${lastSyncedUserId} -> ${userInfo.id}). Сбрасываем таймер."
                    )
                    lastSyncTime = 0L
                }

                Log.d("SyncV", "Триггер сработал! Проверяем тайм-аут внутри performSync...")
                performSync(userInfo.id)
            }
    }

    private fun observeLifecycle() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                isAppInForeground.value = true
            }

            override fun onStop(owner: LifecycleOwner) {
                isAppInForeground.value = false
            }
        })
    }

    private suspend fun performSync(currentUserId: String) {
        val now = System.currentTimeMillis()
        if (now - lastSyncTime < 1 * 60 * 1000 - 3000) {
            Log.d("SyncV", "Слишком рано для синхронизации, скипаем.")
            return
        }
        if (!syncMutex.tryLock()) return
        try {
            val result = syncDataUseCase(userId = currentUserId)
            if (result.isSuccess) {
                Log.d("SyncV", "Синхронизация прошла успешно!")
                lastSyncTime = System.currentTimeMillis()
                // Запоминаем, для какого именно юзера эта отметка времени валидна
                lastSyncedUserId = currentUserId
            } else {
                Log.e("SyncV", "Ошибка юзкейса, сбрасываем таймер")
                lastSyncTime = 0L
            }
        } catch (e: Exception) {
            Log.e("SyncManager", "Error during performSync", e)
        } finally {
            syncMutex.unlock()
        }
    }
}