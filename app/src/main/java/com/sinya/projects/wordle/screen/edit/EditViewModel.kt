package com.sinya.projects.wordle.screen.edit

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.data.local.dao.ProfilesDao
import com.sinya.projects.wordle.data.supabase.entity.Profiles
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditViewModel(
    private val supabase: SupabaseClient,
    private val profileDao: ProfilesDao
) : ViewModel() {
    private val _state = mutableStateOf(EditUiState())
    val state: State<EditUiState> = _state

    companion object {
        fun provideFactory(
            profileDao: ProfilesDao,
            supabase: SupabaseClient
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return EditViewModel(supabase, profileDao) as T
                }
            }
        }
    }

    fun onEvent(event: EditUiEvent) {
        when (event) {
            is EditUiEvent.EmailChanged -> {
                _state.value = _state.value.copy(email = event.value, isEmailError = false)
            }

            is EditUiEvent.NicknameChanged -> {
                _state.value = _state.value.copy(nickname = event.value, isNicknameError = false)
            }

            is EditUiEvent.EditClicked -> {
                _state.value = _state.value.copy(
                    isLoading = true,
                    errorMessage = null
                )
                editUser(
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

            is EditUiEvent.ErrorDismissed -> {
                _state.value = _state.value.copy(
                    errorMessage = null
                )
            }
        }
    }

    private fun editUser(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (validationForm()) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    if (supabase.auth.currentUserOrNull() != null) {
                        supabase.from("profiles").update(
                            {
                                Profiles::nickname setTo _state.value.nickname
                            }
                        ) {
                            filter {
                                Profiles::id eq supabase.auth.currentUserOrNull()!!.id
                            }
                        }
                        profileDao.updateProfile(
                            nickname = _state.value.nickname,
                            id = supabase.auth.currentUserOrNull()!!.id
                        )
                        withContext(Dispatchers.Main) { onSuccess() }
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        onError(e.localizedMessage ?: "Ошибка регистрации")
                    }
                }
            }
        }
    }

    private fun validationForm(): Boolean {
        _state.value = _state.value.copy(
//            email = _state.value.email.trim(),
            nickname = _state.value.nickname.trim(),
        )

        _state.value = _state.value.copy(
//            isEmailError = _state.value.email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(_state.value.email)
//                .matches(),
            isNicknameError = _state.value.nickname.isEmpty()
        )

        return !(
//                _state.value.isEmailError ||
                _state.value.isNicknameError)
    }
}
