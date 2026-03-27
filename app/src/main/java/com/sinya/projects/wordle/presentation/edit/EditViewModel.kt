package com.sinya.projects.wordle.presentation.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.domain.useCase.UpdateNicknameUseCase
import com.sinya.projects.wordle.utils.getErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class EditViewModel @Inject constructor(
    private val updateNicknameUseCase: UpdateNicknameUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<EditUiState>(EditUiState.EditForm())
    val state: StateFlow<EditUiState> = _state.asStateFlow()

    fun onEvent(event: EditEvent) {
        when (event) {
            is EditEvent.NicknameChanged -> updateIfEditForm {
                it.copy(
                    nickname = event.value,
                    isNicknameError = false
                )
            }

            EditEvent.EditClicked -> updateNickname()

            EditEvent.ErrorShown -> updateIfEditForm {
                it.copy(errorMessage = null)
            }
        }
    }

    private fun updateNickname() {
        if (!validateForm()) return

        val formState = _state.value as? EditUiState.EditForm ?: return

        viewModelScope.launch {
            updateIfEditForm {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            updateNicknameUseCase(formState.nickname).fold(
                onSuccess = {
                    _state.value = EditUiState.Success
                },
                onFailure = { error ->
                    updateIfEditForm {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.getErrorMessage()
                        )
                    }
                }
            )
        }
    }

    private fun validateForm(): Boolean {
        val formState = _state.value as? EditUiState.EditForm ?: return false

        val nickname = formState.nickname.trim()
        val isNicknameValid = nickname.isNotEmpty()

        updateIfEditForm {
            formState.copy(
                nickname = nickname,
                isNicknameError = !isNicknameValid
            )
        }

        return isNicknameValid
    }

    private fun updateIfEditForm(transform: (EditUiState.EditForm) -> EditUiState.EditForm) {
        _state.update { currentState ->
            if (currentState is EditUiState.EditForm) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }
}
