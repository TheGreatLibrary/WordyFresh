package com.sinya.projects.wordle.screen.resetEmail

import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.data.local.database.AppDatabase
import com.sinya.projects.wordle.screen.emailConfirm.EmailConfirmUiState
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ResetEmailViewModel(
    private val supabase: SupabaseClient,
    private val db: AppDatabase
) : ViewModel() {

    private val _state = mutableStateOf<ResetEmailUiState>(ResetEmailUiState.ResetForm())
    val state: State<ResetEmailUiState> = _state

    companion object {
        fun provideFactory(
            db: AppDatabase,
            supabase: SupabaseClient
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ResetEmailViewModel(supabase, db) as T
                }
            }
        }
    }

    fun onEvent(event: ResetEmailUiEvent) {
        val currentState = _state.value
        if (currentState !is ResetEmailUiState.ResetForm) return

        when (event) {
            is ResetEmailUiEvent.EmailChanged -> {
                _state.value = currentState.copy(newEmail = event.value, isNewEmailError = false)
            }

            is ResetEmailUiEvent.ResetClicked -> {
                updateEmail(
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

            is ResetEmailUiEvent.ErrorDismissed -> {
                _state.value = currentState.copy(
                    errorMessage = null
                )
            }
        }
    }

    private fun validationEmail(): Boolean {
        val formState = _state.value as? ResetEmailUiState.ResetForm ?: return false

        val trimmed = formState.copy(
            newEmail = formState.newEmail.trim(),
        )

        val updated = trimmed.copy(
            isNewEmailError = trimmed.newEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(trimmed.newEmail)
                .matches(),
        )

        _state.value = updated

        return !updated.isNewEmailError
    }

    private fun updateEmail(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (!validationEmail()) return

        val formState = _state.value as? ResetEmailUiState.ResetForm ?: return

        _state.value = ResetEmailUiState.LoadingConfirm(
            email = formState.newEmail
        )

        val confirm = _state.value as? ResetEmailUiState.LoadingConfirm ?: return

        val session = supabase.auth.currentSessionOrNull()
        Log.d("reset", "session = $session")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val session = supabase.auth.currentSessionOrNull()
                Log.d("reset", "session = $session")
                supabase.auth.updateUser(
                    redirectUrl = "wordy-fresh://reset-email"
                ) {
                    this.email = confirm.email

                }
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Ошибка регистрации")
            }
        }
    }
}