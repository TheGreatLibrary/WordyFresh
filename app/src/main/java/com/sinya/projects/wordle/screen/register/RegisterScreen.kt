package com.sinya.projects.wordle.screen.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sinya.projects.wordle.WordyApplication
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.screen.register.subscreens.LoadingEmailConfirm
import com.sinya.projects.wordle.screen.register.subscreens.RegisterForm
import com.sinya.projects.wordle.ui.theme.WordyShapes
import com.sinya.projects.wordle.ui.theme.white
import io.github.jan.supabase.SupabaseClient

@Composable
fun RegisterScreen(
    navigateToBackStack: () -> Unit,
    navigateTo: (ScreenRoute) -> Unit,
    supabase: SupabaseClient,
    snackbarHost: SnackbarHostState,
    onRegistered: () -> Unit
) {
    val viewModel: RegisterViewModel = viewModel(
        factory = RegisterViewModel.provideFactory(
            WordyApplication.database,
            supabase
        )
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        when (val state = viewModel.state.value) {
            is RegisterUiState.RegisterForm -> {
                LaunchedEffect(state.errorMessage) {
                    state.errorMessage?.let {
                        snackbarHost.showSnackbar(it)
                        viewModel.onEvent(RegisterUiEvent.ErrorDismissed)
                    }
                }

                RegisterForm(
                    navigateToBackStack = navigateToBackStack,
                    navigateTo = navigateTo,
                    onRegisterIn = onRegistered,
                    state = state,
                    onEvent = viewModel::onEvent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(white, WordyShapes.extraLarge)
                        .padding(horizontal = 26.dp, vertical = 14.dp)
                )
            }

            is RegisterUiState.LoadingConfirm -> {
                state.errorMessage?.let {
                    val text = stringResource(it)
                    LaunchedEffect(it) {
                        snackbarHost.showSnackbar(text)
                        viewModel.onEvent(RegisterUiEvent.ErrorDismissed)
                    }
                }

                LoadingEmailConfirm(
                    email = state.email,
                    resendStatus = state.resendStatus,
                    resendState = state.resendState,
                    timer = state.timer,
                    onResendClick = {
                        viewModel.onEvent(RegisterUiEvent.ResendMail(onRegistered))
                        viewModel.onEvent(RegisterUiEvent.ResendStateChange(false))
                        viewModel.onEvent(RegisterUiEvent.TimerTic(60))
                    },
                    onTimerTic = { tic ->
                        viewModel.onEvent(RegisterUiEvent.TimerTic(tic))
                    },
                    onTimerStop = {
                        viewModel.onEvent(RegisterUiEvent.ResendStateChange(true))
                    }
                )
            }
        }
    }
}

