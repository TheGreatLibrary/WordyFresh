package com.sinya.projects.wordle.screen.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sinya.projects.wordle.data.repository.AvatarRepository
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.utils.sendSupportEmail
import io.github.jan.supabase.SupabaseClient

@Composable
fun HomeScreen(
    navigateTo: (ScreenRoute) -> Unit,
    supabase: SupabaseClient
) {
    val context = LocalContext.current

    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.provideFactory(
            supabase,
            AvatarRepository(supabase, context)
        )
    )

    LaunchedEffect(Unit) {
        viewModel.loadAvatar()
        viewModel.loadSaveGame(context)
    }

    HomeScreenView(
        state = viewModel.uiState.value,
        navigateTo = navigateTo,
        sendEmail = { sendSupportEmail(context) },
        onEvent = viewModel::onEvent
    )
}

