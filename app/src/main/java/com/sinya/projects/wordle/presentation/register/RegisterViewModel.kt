package com.sinya.projects.wordle.presentation.register

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.domain.useCase.CheckEmailExistsUseCase
import com.sinya.projects.wordle.domain.useCase.ResendEmailUseCase
import com.sinya.projects.wordle.domain.useCase.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val resendEmailUseCase: ResendEmailUseCase,
    private val checkEmailExistsUseCase: CheckEmailExistsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<RegisterUiState>(RegisterUiState.RegisterForm())
    val state: StateFlow<RegisterUiState> = _state.asStateFlow()

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

            is RegisterEvent.CheckboxStatusChanged -> {
                updateIfRegisterForm {
                    it.copy(checkboxStatus = event.value, isCheckboxError = false)
                }
            }

            RegisterEvent.RegisterClicked -> registerUser()

            RegisterEvent.ErrorShown ->  updateIfRegisterForm {
                it.copy(errorMessage = null)
            }
        }
    }

    private fun validateForm(): Boolean {
        val formState = _state.value as? RegisterUiState.RegisterForm ?: return false

        val email = formState.email.trim()
        val password = formState.password.trim()

        val isEmailValid = email.isNotEmpty() &&
                Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordValid = password.length >= 6
        val isCheckboxChecked = formState.checkboxStatus

        _state.update {
            formState.copy(
                email = email,
                password = password,
                isEmailError = !isEmailValid,
                isPasswordError = !isPasswordValid,
                isCheckboxError = !isCheckboxChecked
            )
        }

        return isEmailValid && isPasswordValid && isCheckboxChecked
    }

    private fun registerUser() {
        if (!validateForm()) return

        val formState = _state.value as? RegisterUiState.RegisterForm ?: return

        viewModelScope.launch {
            checkEmailExistsUseCase(formState.email).fold(
                onSuccess = { exist ->
                    if (exist) {
                        Log.d("Register", "Почта уже есть, пробуем отправить письмо")
                        _state.value = RegisterUiState.LoadingConfirm(email = formState.email)
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
                _state.value = RegisterUiState.LoadingConfirm(email = formState.email)
            },
            onFailure = { error ->
                Log.e("Register", "Ошибка регистрации", error)
                _state.value = RegisterUiState.RegisterForm(
                    email = formState.email,
                    password = formState.password,
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
                },
                onFailure = { error ->
                    Log.e("Register", "Ошибка отправки письма", error)
                    _state.update { RegisterUiState.RegisterForm(currentState.email) }
                }
            )
        }
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
}



