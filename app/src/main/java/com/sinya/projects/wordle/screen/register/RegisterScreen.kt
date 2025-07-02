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

    val state = viewModel.state.value

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (state) {
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

            is RegisterUiState.LoadingConfirm -> LoadingEmailConfirm(email = state.email)
        }
    }
}

