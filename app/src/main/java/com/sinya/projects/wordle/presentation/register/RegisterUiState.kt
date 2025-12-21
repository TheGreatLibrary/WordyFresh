package com.sinya.projects.wordle.presentation.register

import com.sinya.projects.wordle.domain.enums.ResendStatus
import io.github.jan.supabase.auth.user.UserInfo

sealed interface RegisterUiState {
    data class RegisterForm(
        val email: String = "",
        val password: String = "",
        val nickname: String = "",
        val checkboxStatus: Boolean = false,
        val isEmailError: Boolean = false,
        val isPasswordError: Boolean = false,
        val isNickNameError: Boolean = false,
        val isCheckboxError: Boolean = false,
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    ) : RegisterUiState

    data class LoadingConfirm(
        val email: String,
        val password: String,
        val nickname: String,
        val errorMessage: String? = null,
        val resendStatus: ResendStatus = ResendStatus.Idle,
        val resendEnabled: Boolean = false,
        val timer: Int = 60
    ) : RegisterUiState

    data class Success(
        val user: UserInfo
    ) : RegisterUiState
}

