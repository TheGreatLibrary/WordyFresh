package com.sinya.projects.wordle.presentation.register

import io.github.jan.supabase.auth.user.UserInfo

sealed interface RegisterUiState {
    data class RegisterForm(
        val email: String = "",
        val password: String = "",
        val checkboxStatus: Boolean = false,
        val isEmailError: Boolean = false,
        val isPasswordError: Boolean = false,
        val isCheckboxError: Boolean = false,
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    ) : RegisterUiState

    data class LoadingConfirm(
        val email: String
    ) : RegisterUiState

    data class Success(
        val user: UserInfo
    ) : RegisterUiState
}

