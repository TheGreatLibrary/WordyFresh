package com.sinya.projects.wordle.data.remote.supabase

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.domain.source.SupabaseAuthDataSource
import com.sinya.projects.wordle.domain.useCase.SyncDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.status.SessionStatus
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val syncDataUseCase: SyncDataUseCase,
    private val authDataSource: SupabaseAuthDataSource
) : ViewModel() {

    private var isAuthenticated = false

    init {
        observeSessionChanges()
        observeAppLifecycle()
    }

    private fun observeSessionChanges() = viewModelScope.launch {
        authDataSource.sessionStatusFlow().collect { status ->
            val wasAuthenticated = isAuthenticated
            isAuthenticated = status is SessionStatus.Authenticated

            if (isAuthenticated && !wasAuthenticated) {
                Log.d("SyncViewModel", "Пользователь авторизован, синхронизация")
                delay(500)
                performSync()
            }
        }
    }

    private suspend fun performSync() {
        syncDataUseCase().fold(
            onSuccess = { Log.d("SyncViewModel", "✅ Синхронизация успешна") },
            onFailure = { Log.e("SyncViewModel", "❌ Ошибка: ${it.message}") }
        )
    }

    private fun observeAppLifecycle() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                viewModelScope.launch {
                    if (authDataSource.getCurrentUser() != null) {
                        Log.d("SyncViewModel", "📱 Возврат из фона - синхронизация")
                        performSync()
                    }
                }
            }
        })
    }
}