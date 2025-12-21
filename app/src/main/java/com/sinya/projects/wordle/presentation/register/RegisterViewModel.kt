package com.sinya.projects.wordle.presentation.register

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.data.local.achievement.AchievementTrigger
import com.sinya.projects.wordle.data.remote.supabase.entity.Profiles
import com.sinya.projects.wordle.data.remote.web.LegalLinks
import com.sinya.projects.wordle.domain.enums.ResendStatus
import com.sinya.projects.wordle.domain.useCase.CheckAchievementUseCase
import com.sinya.projects.wordle.domain.useCase.CheckEmailExistsUseCase
import com.sinya.projects.wordle.domain.useCase.InsertProfileUseCase
import com.sinya.projects.wordle.domain.useCase.LoginUseCase
import com.sinya.projects.wordle.domain.useCase.ResendEmailUseCase
import com.sinya.projects.wordle.domain.useCase.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val signInUseCase: LoginUseCase,
    private val resendEmailUseCase: ResendEmailUseCase,
    private val insertProfileUseCase: InsertProfileUseCase,
    private val checkAchievementUseCase: CheckAchievementUseCase,
    private val checkEmailExistsUseCase: CheckEmailExistsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<RegisterUiState>(RegisterUiState.RegisterForm())
    val state: StateFlow<RegisterUiState> = _state.asStateFlow()

    private var pollingJob: Job? = null

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.EmailChanged -> {
                updateIfRegisterForm {
                    it.copy(email = event.value, isEmailError = false)
                }
            }

            is RegisterEvent.PasswordChanged -> {
                updateIfRegisterForm {
                    it.copy(password = event.value, isPasswordError = false)
                }
            }

            is RegisterEvent.NicknameChanged -> {
                updateIfRegisterForm {
                    it.copy(nickname = event.value, isNickNameError = false)
                }
            }

            is RegisterEvent.CheckboxStatusChanged -> {
                updateIfRegisterForm {
                    it.copy(checkboxStatus = event.value, isCheckboxError = false)
                }
            }

            RegisterEvent.RegisterClicked -> registerUser()

            RegisterEvent.ResendMail -> resendEmail()

            RegisterEvent.TimerTick -> {
                updateIfLoadingConfirm { state ->
                    val newTimer = (state.timer - 1).coerceAtLeast(0)
                    state.copy(
                        timer = newTimer,
                        resendEnabled = newTimer == 0
                    )
                }
            }

            RegisterEvent.ErrorShown -> {
                if (_state.value is RegisterUiState.RegisterForm) {
                    _state.update {
                        (it as RegisterUiState.RegisterForm).copy(errorMessage = null)
                    }
                }
                else if (_state.value is RegisterUiState.LoadingConfirm) {
                    _state.update {
                        (it as RegisterUiState.LoadingConfirm).copy(errorMessage = null)
                    }
                }
            }

            RegisterEvent.RegistrationSuccess -> { }
        }
    }

    private fun validateForm(): Boolean {
        val formState = _state.value as? RegisterUiState.RegisterForm ?: return false

        val email = formState.email.trim()
        val password = formState.password.trim()
        val nickname = formState.nickname.trim()

        val isEmailValid = email.isNotEmpty() &&
                Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordValid = password.length >= 6
        val isNicknameValid = nickname.isNotEmpty()
        val isCheckboxChecked = formState.checkboxStatus

        _state.update {
            formState.copy(
                email = email,
                password = password,
                nickname = nickname,
                isEmailError = !isEmailValid,
                isPasswordError = !isPasswordValid,
                isNickNameError = !isNicknameValid,
                isCheckboxError = !isCheckboxChecked
            )
        }

        return isEmailValid && isPasswordValid && isNicknameValid && isCheckboxChecked
    }

    private fun registerUser() {
        if (!validateForm()) return

        val formState = _state.value as? RegisterUiState.RegisterForm ?: return

        viewModelScope.launch {
            checkEmailExistsUseCase(formState.email).fold(
                onSuccess = { exist ->
                    if (exist) {
                        Log.d("Register", "Почта уже есть, пробуем отправить письмо")
                        _state.value = RegisterUiState.LoadingConfirm(
                            email = formState.email,
                            password = formState.password,
                            nickname = formState.nickname
                        )
                        resendEmail()
                    } else {
                        Log.d("Register", "Почты нет, регаемся")
                        proceedWithSignUp(formState)
                    }
                },
                onFailure = {
                    Log.d("Register", "Ошибка??? Почты нет? регаемся")
                    proceedWithSignUp(formState)
                }
            )
        }
    }

    private fun proceedWithSignUp(formState: RegisterUiState.RegisterForm) = viewModelScope.launch {
        signUpUseCase(formState.email, formState.password).fold(
            onSuccess = {
                Log.d("Register", "Зарегистрировались!")
                _state.value = RegisterUiState.LoadingConfirm(
                    email = formState.email,
                    password = formState.password,
                    nickname = formState.nickname
                )
                startPolling(
                    email = formState.email,
                    password = formState.password,
                    nickname = formState.nickname
                )
            },
            onFailure = { error ->
                Log.e("Register", "Ошибка регистрации", error)
                _state.value = RegisterUiState.RegisterForm(
                    email = formState.email,
                    password = formState.password,
                    nickname = formState.nickname,
                    isLoading = false,
                    errorMessage = error.localizedMessage ?: "Ошибка регистрации"
                )
            }
        )
    }

    private fun resendEmail() {
        val currentState = _state.value as? RegisterUiState.LoadingConfirm ?: return

        viewModelScope.launch {
            resendEmailUseCase(currentState.email).fold(
                onSuccess = {
                    Log.d("Register", "Отправляем письмо!")
                    _state.update {
                        currentState.copy(
                            resendStatus = ResendStatus.Sent,
                            timer = 60,
                            resendEnabled = false
                        )
                    }
                    startPolling(
                        email = currentState.email,
                        password = currentState.password,
                        nickname = currentState.nickname,
                    )
                },
                onFailure = { error ->
                    Log.e("Register", "Ошибка отправки письма", error)
                    _state.update {
                        currentState.copy(
                            resendStatus = ResendStatus.Error
                        )
                    }
                }
            )
        }
    }

    private fun startPolling(
        email: String,
        password: String,
        nickname: String
    ) {
        pollingJob?.cancel()

        pollingJob = viewModelScope.launch {
            var attempts = 0
            val maxAttempts = 600

            while (isActive && attempts < maxAttempts) {
                delay(1000)
                attempts++

                try {
                    signInUseCase(email, password).fold(
                        onSuccess = { user ->
                            if (user != null) {
                                Log.d("Register", "Вошли!")

                                val profile = Profiles(
                                    id = user.id,
                                    nickname = nickname,
                                    avatarUrl = LegalLinks.getAvatarFileName(user.id),
                                    createdAt = Clock.System.now().toString()
                                )

                                insertProfileUseCase(profile).fold(
                                    onSuccess = {
                                        Log.d("RegisterVM", "Profile created")
                                    },
                                    onFailure = { error ->
                                        Log.e("RegisterVM", "Profile creation failed", error)
                                    }
                                )

                                checkAchievementUseCase(AchievementTrigger.AccountRegistered)
                                _state.value = RegisterUiState.Success(user = user)

                                return@launch
                            }
                        },
                        onFailure = { }
                    )
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.e("RegisterVM", "Polling error", e)
                }
            }

            if (isActive) {
                _state.update { currentState ->
                    if (currentState is RegisterUiState.LoadingConfirm) {
                        currentState.copy(
                            resendStatus = ResendStatus.NotConfirmed,
                            errorMessage = "Время ожидания истекло. Попробуйте отправить письмо снова."
                        )
                    } else {
                        currentState
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }

    private fun updateIfRegisterForm(transform: (RegisterUiState.RegisterForm) -> RegisterUiState.RegisterForm) {
        _state.update { currentState ->
            if (currentState is RegisterUiState.RegisterForm) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }

    private fun updateIfLoadingConfirm(transform: (RegisterUiState.LoadingConfirm) -> RegisterUiState.LoadingConfirm) {
        _state.update { currentState ->
            if (currentState is RegisterUiState.LoadingConfirm) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }
}



