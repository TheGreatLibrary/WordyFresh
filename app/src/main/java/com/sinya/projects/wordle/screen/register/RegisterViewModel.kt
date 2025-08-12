package com.sinya.projects.wordle.screen.register

import android.util.Patterns
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.data.local.achievement.AchievementTrigger
import com.sinya.projects.wordle.data.local.achievement.objects.AchievementManager
import com.sinya.projects.wordle.data.local.database.AppDatabase
import com.sinya.projects.wordle.data.remote.supabase.SupabaseService
import com.sinya.projects.wordle.data.remote.supabase.entity.Profiles
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.OtpType
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
        when (val currentState = _state.value) {
            is RegisterUiState.RegisterForm -> {
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
                        _state.value = currentState.copy(isLoading = true, errorMessage = null)
                        registerUser(
                            onSuccess = {
                                _state.value = currentState.copy(isLoading = false)
                                event.success()
                                viewModelScope.launch {
                                    AchievementManager.onTrigger(
                                        AchievementTrigger.AccountRegistered,
                                        db.loadStats()
                                    )
                                }
                            },
                            onError = {
                                _state.value = currentState.copy(isLoading = false, errorMessage = it)
                            }
                        )
                    }

                    is RegisterUiEvent.ErrorDismissed -> {
                        _state.value = currentState.copy(errorMessage = null)
                    }

                    // Не обрабатываем ResendMail здесь, потому что это неактуально в RegisterForm
                    else -> Unit
                }
            }

            is RegisterUiState.LoadingConfirm -> {
                when (event) {
                    is RegisterUiEvent.ResendStateChange -> {
                        _state.value = currentState.copy(resendState = event.state)
                    }
                    is RegisterUiEvent.TimerTic -> {
                        _state.value = currentState.copy(timer = event.tic)
                    }
                    is RegisterUiEvent.ResendMail -> {
                        resendEmail(
                            onSuccess = {
                                event.success()
                                viewModelScope.launch {
                                    AchievementManager.onTrigger(
                                        AchievementTrigger.AccountRegistered,
                                        db.loadStats()
                                    )
                                }
                            },
                        ) { message ->
                            val latestState = _state.value
                            if (latestState is RegisterUiState.LoadingConfirm) {
                                _state.value = latestState.copy(resendStatus = message)
                            }
                        }
                    }

                    // потенциально можно добавить сюда поддержку кнопки "Я уже подтвердил"
                    else -> Unit
                }
            }

            // если добавишь будущие состояния, например EmailUnconfirmed
            else -> Unit
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
                    onError = {
                        _state.value = confirm.copy(errorMessage = it)
                    }
                )
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _state.value = formState.copy(isLoading = false)
                    onError("Ошибка регистрации")
                }
            }
        }
    }

    private suspend fun pollUntilConfirmed(
        email: String,
        password: String,
        nickname: String,
        onSuccess: () -> Unit,
        onError: (Int) -> Unit
    ) {
        var attempts = 0
        val maxAttempts = 300 // 20 секунд максимум

        while (supabase.auth.currentUserOrNull()?.id == null && attempts < maxAttempts) {
            delay(1000)
            attempts++

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
            } catch (_: Exception) {
                // можно логировать если надо
            }
        }

        withContext(Dispatchers.Main) {
            val currentState = _state.value
            if (currentState is RegisterUiState.LoadingConfirm) {
                _state.value = currentState.copy(
                    resendStatus = R.string.resend_status_not_confirmed
                )
            }
            onError(R.string.resend_status_not_confirmed)
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
            isEmailError = trimmed.email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(trimmed.email)
                .matches(),
            isPasswordError = trimmed.password.length < 6,
            isNickNameError = trimmed.nickname.isEmpty()
        )

        _state.value = updated

        return !(updated.isPasswordError || updated.isEmailError ||
                updated.isNickNameError || updated.isCheckboxError)
    }

    private fun resendEmail(
        onSuccess: () -> Unit,
        onResult: (Int?) -> Unit
    ) {
        val currentState = _state.value
        if (currentState !is RegisterUiState.LoadingConfirm) return

        viewModelScope.launch {
            try {
                supabase.auth.resendEmail(OtpType.Email.SIGNUP, email = currentState.email)
                onResult(R.string.resend_status_sent)
                pollUntilConfirmed(
                    email = currentState.email,
                    password = currentState.password,
                    nickname = currentState.nickname,
                    onSuccess = onSuccess,
                    onError = {
                        _state.value = currentState.copy(errorMessage = it)
                    }
                )
            } catch (e: Exception) {
                onResult(R.string.resend_status_error)
            }
        }
    }
}



