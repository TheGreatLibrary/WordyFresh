package com.sinya.projects.wordle.screen.login

import android.util.Patterns
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sinya.projects.wordle.data.local.dao.ProfilesDao
import com.sinya.projects.wordle.data.remote.supabase.SupabaseService
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(
    private val supabase: SupabaseClient,
    private val profilesDao: ProfilesDao
) : ViewModel() {
    private val _state = mutableStateOf(LoginUiState())
    val state: State<LoginUiState> = _state

    companion object {
        fun provideFactory(
            profilesDao: ProfilesDao,
            supabase: SupabaseClient
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return LoginViewModel(supabase, profilesDao) as T
                }
            }
        }
    }

    fun onEvent(event: LoginUiEvent) {
        when(event) {
            is LoginUiEvent.EmailChanged -> {
                _state.value = _state.value.copy(email = event.value, isEmailError = false)
            }
            is LoginUiEvent.PasswordChanged -> {
                _state.value = _state.value.copy(password = event.value, isPasswordError = false)
            }
            is LoginUiEvent.LoginClicked -> {
                _state.value = _state.value.copy(
                    isLoading = true,
                    errorMessage = null
                )
                loginUser(
                    onSuccess = {
                        _state.value = _state.value.copy(
                            isLoading = false
                        )
                        event.success()
                    },
                    onError = {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            errorMessage = it
                        )
                    }
                )

            }
            is LoginUiEvent.ErrorDismissed -> {
                _state.value = _state.value.copy(
                    errorMessage = null
                )
            }
        }
    }

    private fun validationForm() : Boolean {
        _state.value = _state.value.copy(
            email = _state.value.email.trim(),
            password = _state.value.password.trim(),
        )

        _state.value = _state.value.copy(
            isEmailError = _state.value.email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(_state.value.email).matches(),
            isPasswordError = _state.value.password.length<6,
        )

        return !(_state.value.isPasswordError && _state.value.isEmailError)
    }

    private fun loginUser(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (!validationForm()) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                supabase.auth.signInWith(Email) {
                    this.email = _state.value.email
                    this.password = _state.value.password
                }

                val user = supabase.auth.currentUserOrNull()
                    ?: throw IllegalStateException("Ошибка авторизации: пользователь не найден")

                val profile = SupabaseService.fetchProfile(user.id)
                    ?: throw IllegalStateException("Профиль не найден. Возможно, регистрация была прервана.")

                profilesDao.insertProfile(profile)

                withContext(Dispatchers.Main) { onSuccess() }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e.localizedMessage ?: "Ошибка входа")
                }
            }
        }
    }
}
