package com.sinya.projects.wordle.presentation.register

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.presentation.register.subscreen.LoadingEmailConfirmView
import com.sinya.projects.wordle.presentation.register.subscreen.RegisterFormView
import io.github.jan.supabase.auth.user.UserInfo

@Composable
fun RegisterScreen(
    navigateBack: () -> Unit,
    navigateTo: (ScreenRoute) -> Unit,
    viewModel: RegisterViewModel = hiltViewModel(),
    onRegistered: (UserInfo?) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    when (state) {
        is RegisterUiState.Success -> {
            LaunchedEffect(Unit) {
                onRegistered((state as RegisterUiState.Success).user)
            }
        }

        else -> {}
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val currentState = state) {
            is RegisterUiState.RegisterForm -> {
                RegisterFormView(
                    navigateBack = navigateBack,
                    navigateTo = navigateTo,
                    state = currentState,
                    onEvent = viewModel::onEvent,
                    snackbarHostState = snackbarHostState
                )
            }

            is RegisterUiState.LoadingConfirm -> {
                LoadingEmailConfirmView(
                    state = currentState,
                    onEvent = viewModel::onEvent,
                    snackbarHostState = snackbarHostState
                )
            }

            is RegisterUiState.Success -> { }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}
