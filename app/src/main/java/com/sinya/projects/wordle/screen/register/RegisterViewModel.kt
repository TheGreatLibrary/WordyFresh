package com.sinya.projects.wordle.screen.register

import android.util.Patterns
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sinya.projects.wordle.data.local.dao.ProfilesDao
import com.sinya.projects.wordle.domain.model.entity.Profiles
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import java.util.UUID

class RegisterViewModel(
    private val supabase: SupabaseClient,
    private val profileDao: ProfilesDao
) : ViewModel() {
    private val _state = mutableStateOf(RegisterUiState())
    val state: State<RegisterUiState> = _state

    companion object {
        fun provideFactory(
            profileDao: ProfilesDao,
            supabase: SupabaseClient
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return RegisterViewModel(supabase, profileDao) as T
                }
            }
        }
    }

    fun onEvent(event: RegisterUiEvent) {
        when(event) {
            is RegisterUiEvent.EmailChanged -> {
                _state.value = _state.value.copy(email = event.value, isEmailError = false)
            }
            is RegisterUiEvent.PasswordChanged -> {
                _state.value = _state.value.copy(password = event.value, isPasswordError = false)
            }
            is RegisterUiEvent.NicknameChanged -> {
                _state.value = _state.value.copy(nickname = event.value, isNickNameError = false)
            }
            is RegisterUiEvent.CheckboxStatusChanged -> {
                _state.value = _state.value.copy(checkboxStatus = event.value, isCheckboxError = false)
            }
            is RegisterUiEvent.RegisterClicked -> {
                _state.value = _state.value.copy(
                    isLoading = true,
                    errorMessage = null
                )
                registerUser(
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
            is RegisterUiEvent.ErrorDismissed -> {
                _state.value = _state.value.copy(
                    errorMessage = null
                )
            }
        }
    }

    private fun registerUser(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (validationForm()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    supabase.auth.signUpWith(Email) {
                        this.email = _state.value.email
                        this.password = _state.value.password
                    }
                    if (supabase.auth.currentUserOrNull() != null) {
                        supabase.from("profiles").insert(
                            Profiles(
                                id = supabase.auth.currentUserOrNull()!!.id,
                                nickname = _state.value.nickname,
                                avatarUrl = "",
                                createdAt = Clock.System.now().toString()
                            )
                        )
                        profileDao.insertProfile(
                            Profiles(
                                id = supabase.auth.currentUserOrNull()!!.id,
                                nickname = _state.value.nickname,
                                avatarUrl = "",
                                createdAt = Clock.System.now().toString()
                            )
                        )
                        withContext(Dispatchers.Main) { onSuccess() }
                    }
                    else {
                        supabase.auth.signInWith(Email) {
                            this.email = _state.value.email
                            this.password = _state.value.password
                        }
                        supabase.from("profiles").insert(
                            Profiles(
                                id = supabase.auth.currentUserOrNull()?.id ?: UUID.randomUUID().toString(),
                                nickname = _state.value.nickname,
                                avatarUrl = "",
                                createdAt = Clock.System.now().toString()
                            )
                        )
                        profileDao.insertProfile(
                            Profiles(
                                id = supabase.auth.currentUserOrNull()?.id ?: UUID.randomUUID().toString(),
                                nickname = _state.value.nickname,
                                avatarUrl = "",
                                createdAt = Clock.System.now().toString()
                            )
                        )
                        withContext(Dispatchers.Main) {
                            onSuccess()
                        }
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        onError(e.localizedMessage ?: "Ошибка регистрации")
                    }
                }
            }
        }
    }

    private fun validationForm() : Boolean {
        _state.value = _state.value.copy(
            email = _state.value.email.trim(),
            password = _state.value.password.trim(),
            nickname = _state.value.nickname.trim(),
        )

        _state.value = _state.value.copy(
            isCheckboxError = !_state.value.checkboxStatus,
            isEmailError = _state.value.email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(_state.value.email).matches(),
            isPasswordError = _state.value.password.length<6,
            isNickNameError = _state.value.nickname.isEmpty()
        )

        return !(_state.value.isPasswordError || _state.value.isEmailError ||
                _state.value.isNickNameError || _state.value.isCheckboxError)
    }
}
