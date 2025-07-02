package com.sinya.projects.wordle.screen.resetPassword

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.data.local.database.AppDatabase
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResetPasswordViewModel(
    private val supabase: SupabaseClient,
    private val db: AppDatabase
) : ViewModel() {

    private val _state = mutableStateOf<ResetPasswordUiState>(ResetPasswordUiState.ResetForm())
    val state: State<ResetPasswordUiState> = _state

    companion object {
        fun provideFactory(
            db: AppDatabase,
            supabase: SupabaseClient
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ResetPasswordViewModel(supabase, db) as T
                }
            }
        }
    }

    fun onEvent(event: ResetPasswordUiEvent) {
        val currentState = _state.value
        if (currentState !is ResetPasswordUiState.ResetForm) return

        when (event) {
            is ResetPasswordUiEvent.PasswordChanged -> {
                _state.value = currentState.copy(newPassword = event.value, isNewPasswordError = false)
            }

            is ResetPasswordUiEvent.RepeatPasswordChanged -> {
                _state.value = currentState.copy(repeatNewPassword = event.value, isRepeatNewPasswordError = false)
            }

            is ResetPasswordUiEvent.ResetClicked -> {
                updatePassword(
                    onSuccess = {
                        event.success()
                    },
                    onError = {
                        _state.value = currentState.copy(
                            errorMessage = it
                        )
                    }
                )
            }

            is ResetPasswordUiEvent.ErrorDismissed -> {
                _state.value = currentState.copy(
                    errorMessage = null
                )
            }
        }
    }

    private fun validationForm(): Boolean {
        val formState = _state.value as? ResetPasswordUiState.ResetForm ?: return false

        val trimmed = formState.copy(
            newPassword = formState.newPassword.trim(),
            repeatNewPassword = formState.repeatNewPassword.trim()
        )

        val updated = trimmed.copy(
            isNewPasswordError = trimmed.newPassword.length < 6 || trimmed.repeatNewPassword != trimmed.newPassword,
            isRepeatNewPasswordError = trimmed.repeatNewPassword.length < 6 || trimmed.repeatNewPassword != trimmed.newPassword
        )

        _state.value = updated

        return !(updated.isNewPasswordError || updated.isRepeatNewPasswordError)
    }

    private fun updatePassword(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (!validationForm()) return
        val formState = _state.value as? ResetPasswordUiState.ResetForm ?: return

        Log.d("reset", "пробуем обновить")

        val session = supabase.auth.currentSessionOrNull()
        Log.d("reset", "session = $session")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val session = supabase.auth.currentSessionOrNull()
                Log.d("reset", "session = $session")
                supabase.auth.updateUser {
                    this.password = formState.newPassword
                }
                Log.d("reset", "обновляем пароль")
                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Ошибка регистрации")
            }
        }
    }
}