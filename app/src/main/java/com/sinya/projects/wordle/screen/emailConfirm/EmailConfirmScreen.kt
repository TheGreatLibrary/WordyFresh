package com.sinya.projects.wordle.screen.emailConfirm

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sinya.projects.wordle.screen.register.subscreens.LoadingEmailConfirm
import io.github.jan.supabase.SupabaseClient

@Composable
fun EmailConfirmScreen(
    navigateTo: () -> Unit,
    navigateToBackStack: () -> Unit,
    supabase: SupabaseClient
) {
    val viewModel: EmailConfirmViewModel = viewModel(
        factory = EmailConfirmViewModel.provideFactory(supabase)
    )

    val state = viewModel.state.value
    Box(modifier = Modifier.fillMaxSize()) {
        when(state) {
            is EmailConfirmUiState.PutEmailToRecovery -> EmailConfirmScreenView(
                navigateToBackStack = navigateToBackStack,
                state = state,
                onEvent = viewModel::onEvent
            )
            is EmailConfirmUiState.LoadingConfirm -> LoadingEmailConfirm(
                email = state.email
            )
        }
    }
}