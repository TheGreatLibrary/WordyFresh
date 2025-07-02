package com.sinya.projects.wordle.screen.register

import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.data.local.achievement.AchievementTrigger
import com.sinya.projects.wordle.data.local.achievement.objects.AchievementManager
import com.sinya.projects.wordle.data.local.database.AppDatabase
import com.sinya.projects.wordle.data.remote.supabase.SupabaseService
import com.sinya.projects.wordle.domain.model.entity.Profiles
import com.sinya.projects.wordle.ui.theme.white
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class RegisterViewModel(
    private val supabase: SupabaseClient,
    private val db: AppDatabase
) : ViewModel() {

    private val _state = mutableStateOf<RegisterUiState>(RegisterUiState.RegisterForm())
    val state: State<RegisterUiState> = _state

    companion object {
        fun provideFactory(
            db: AppDatabase,
            supabase: SupabaseClient
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return RegisterViewModel(supabase, db) as T
                }
            }
        }
    }

    fun onEvent(event: RegisterUiEvent) {
        val currentState = _state.value
        if (currentState !is RegisterUiState.RegisterForm) return

        when (event) {
            is RegisterUiEvent.EmailChanged -> {
                _state.value = currentState.copy(email = event.value, isEmailError = false)
            }

            is RegisterUiEvent.PasswordChanged -> {
                _state.value = currentState.copy(password = event.value, isPasswordError = false)
            }

            is RegisterUiEvent.NicknameChanged -> {
                _state.value = currentState.copy(nickname = event.value, isNickNameError = false)
            }

            is RegisterUiEvent.CheckboxStatusChanged -> {
                _state.value = currentState.copy(checkboxStatus = event.value, isCheckboxError = false)
            }

            is RegisterUiEvent.RegisterClicked -> {
                _state.value = currentState.copy(
                    isLoading = true,
                    errorMessage = null
                )
                registerUser(
                    onSuccess = {
                        _state.value = currentState.copy(
                            isLoading = false
                        )
                        event.success()
                        viewModelScope.launch {
                            AchievementManager.onTrigger(
                                AchievementTrigger.AccountRegistered,
                                db.loadStats()
                            )
                        }
                    },
                    onError = {
                        _state.value = currentState.copy(
                            isLoading = false,
                            errorMessage = it
                        )
                    }
                )
            }

            is RegisterUiEvent.ErrorDismissed -> {
                _state.value = currentState.copy(
                    errorMessage = null
                )
            }
        }
    }

    private fun registerUser(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (!validationForm()) return

        val formState = _state.value as? RegisterUiState.RegisterForm ?: return

        // переходим во 2 фазу
        _state.value = RegisterUiState.LoadingConfirm(
            email = formState.email,
            password = formState.password,
            nickname = formState.nickname
        )

        val confirm = _state.value as? RegisterUiState.LoadingConfirm ?: return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                supabase.auth.signUpWith(Email) {
                    email = confirm.email
                    password = confirm.password
                }

                pollUntilConfirmed(
                    email = confirm.email,
                    password = confirm.password,
                    nickname = confirm.nickname,
                    onSuccess = onSuccess,
                    onError = onError
                )
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _state.value = formState.copy(isLoading = false)
                    onError(e.localizedMessage ?: "Ошибка регистрации")
                }
            }
        }
    }

    private suspend fun pollUntilConfirmed(
        email: String,
        password: String,
        nickname: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        Log.d("ААА", "еще раз!!!!!")
        while(supabase.auth.currentUserOrNull()?.id==null) {
            delay(2000)
            Log.d("ААА", "еще раз!")
            try {
                supabase.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }

                val user = supabase.auth.currentUserOrNull()
                if (user != null) {
                    val profile = Profiles(
                        id = user.id,
                        nickname = nickname,
                        avatarUrl = "",
                        createdAt = Clock.System.now().toString()
                    )

                    SupabaseService.insertNewProfile(profile)
                    db.profilesDao().insertProfile(profile)

                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                    return
                }
            } catch (_: Exception) { }
        }

        withContext(Dispatchers.Main) {
            _state.value = RegisterUiState.RegisterForm(
                email = email,
                password = password,
                nickname = nickname
            )
            onError("Email не был подтвержден. Попробуйте позже.")
        }
    }

    private fun validationForm(): Boolean {
        val formState = _state.value as? RegisterUiState.RegisterForm ?: return false

        val trimmed = formState.copy(
            email = formState.email.trim(),
            password = formState.password.trim(),
            nickname = formState.nickname.trim()
        )

        val updated = trimmed.copy(
            isCheckboxError = !trimmed.checkboxStatus,
            isEmailError = trimmed.email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(trimmed.email).matches(),
            isPasswordError = trimmed.password.length < 6,
            isNickNameError = trimmed.nickname.isEmpty()
        )

        _state.value = updated

        return !(updated.isPasswordError || updated.isEmailError ||
                updated.isNickNameError || updated.isCheckboxError)
    }
}
