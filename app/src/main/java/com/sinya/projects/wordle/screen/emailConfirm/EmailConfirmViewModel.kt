package com.sinya.projects.wordle.screen.emailConfirm

import android.util.Patterns
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EmailConfirmViewModel(private val supabase: SupabaseClient) : ViewModel() {
    private val _state =
        mutableStateOf<EmailConfirmUiState>(EmailConfirmUiState.PutEmailToRecovery())
    val state: State<EmailConfirmUiState> = _state

    companion object {
        fun provideFactory(
            supabase: SupabaseClient
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return EmailConfirmViewModel(supabase) as T
                }
            }
        }
    }

    fun onEvent(event: EmailConfirmUiEvent) {
        val currentState = _state.value
        if (currentState !is EmailConfirmUiState.PutEmailToRecovery) return

        when (event) {
            is EmailConfirmUiEvent.EmailChanged -> {
                _state.value = currentState.copy(
                    email = event.value,
                    isEmailError = false
                )
            }
            is EmailConfirmUiEvent.GoToLoading -> {
                putEmailToConfirm(
                    onError = {
                        _state.value = currentState.copy(
                            errorMessage = it
                        )
                    }
                )
            }
        }
    }

    private fun validationEmail(): Boolean {
        val formState = _state.value as? EmailConfirmUiState.PutEmailToRecovery ?: return false

        val trimmed = formState.copy(
            email = formState.email.trim(),
        )

        val updated = trimmed.copy(
            isEmailError = trimmed.email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(trimmed.email)
                .matches(),
        )

        _state.value = updated

        return !updated.isEmailError
    }

    private fun putEmailToConfirm(
        onError: (String) -> Unit
    ) {
        if (!validationEmail()) return

        val formState = _state.value as? EmailConfirmUiState.PutEmailToRecovery ?: return

        _state.value = EmailConfirmUiState.LoadingConfirm(
            email = formState.email
        )

        val confirm = _state.value as? EmailConfirmUiState.LoadingConfirm ?: return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                supabase.auth.resetPasswordForEmail(
                    confirm.email,
                    redirectUrl = "wordy-fresh://reset-password"
                )
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e.localizedMessage ?: "Ошибка отправки письма")
                }
            }
        }
    }
}
