package com.sinya.projects.wordle.screen.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sinya.projects.wordle.WordyApplication
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.ui.theme.white
import io.github.jan.supabase.SupabaseClient

@Composable
fun LoginScreen(
    navigateToBackStack: () -> Unit,
    navigateTo: (ScreenRoute) -> Unit,
    supabase: SupabaseClient,
    snackbarHost: SnackbarHostState,
    onLoggedIn: () -> Unit,
) {
    val db = WordyApplication.database

    val profileDao = db.profilesDao()

    val viewModel: LoginViewModel = viewModel(
        factory = LoginViewModel.provideFactory(profileDao, supabase)
    )

    val state = viewModel.state.value

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHost.showSnackbar(it)
            viewModel.onEvent(LoginUiEvent.ErrorDismissed)
        }
    }

    LoginScreenView(
        navigateToBackStack = navigateToBackStack,
        navigateTo = navigateTo,
        onLoggedIn = onLoggedIn,
        state = viewModel.state.value,
        onEvent = viewModel::onEvent,
        modifier = Modifier
            .fillMaxWidth()
            .background(white, RoundedCornerShape(100))
            .padding(horizontal = 32.dp, vertical = 16.dp)
    )
}