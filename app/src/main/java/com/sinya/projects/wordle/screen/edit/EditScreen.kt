package com.sinya.projects.wordle.screen.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sinya.projects.wordle.WordyApplication
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.screen.resetPassword.ResetPasswordScreenView
import com.sinya.projects.wordle.screen.resetPassword.ResetPasswordUiState
import com.sinya.projects.wordle.screen.resetPassword.ResetPasswordViewModel
import com.sinya.projects.wordle.ui.theme.WordyShapes
import com.sinya.projects.wordle.ui.theme.white
import io.github.jan.supabase.SupabaseClient

@Composable
fun EditScreen(
    navigateToBackStack: () -> Unit,
    navigateTo: (ScreenRoute) -> Unit,
    supabase: SupabaseClient
) {
    val db = WordyApplication.database
    val viewModel: EditViewModel = viewModel(
        factory = EditViewModel.provideFactory(db, supabase)
    )

    val state = viewModel.state.value

    ResetPasswordScreenView(
        navigateToBackStack = navigateToBackStack,
        state = state,
        onEvent = viewModel::onEvent,
        modifier = Modifier
            .fillMaxWidth()
            .background(white, WordyShapes.extraLarge)
            .padding(horizontal = 26.dp, vertical = 14.dp),
        onReset = { }
    )
}

@Preview(showBackground = true)
@Composable
fun EditScreenPreview() {
    EditScreen(
        navigateTo = { },
        navigateToBackStack = { },
        supabase = WordyApplication.supabaseClient
    )
}