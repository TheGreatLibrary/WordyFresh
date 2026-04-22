package com.sinya.projects.wordle.presentation.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.presentation.profile.subscreen.ProfileInAccount
import com.sinya.projects.wordle.presentation.profile.subscreen.ProfileOutAccount
import com.sinya.projects.wordle.presentation.profile.subscreen.ProfilePlaceholder

@Composable
fun ProfileScreen(
    navigateBack: () -> Unit,
    navigateTo: (ScreenRoute) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        ProfileUiState.Loading -> ProfilePlaceholder(
            title = stringResource(R.string.profile_screen),
            navigateBack = navigateBack
        )

        is ProfileUiState.InAccount -> ProfileInAccount(
            title = stringResource(R.string.profile_screen),
            navigateBack = navigateBack,
            state = state as ProfileUiState.InAccount,
            onEvent = viewModel::onEvent,
            navigateTo = navigateTo,
        )

        is ProfileUiState.NoAccount -> ProfileOutAccount(
            viewModel = viewModel,
            navigateTo = navigateTo,
            onEvent = viewModel::onEvent
        )

        ProfileUiState.CreateProfile -> {
            LaunchedEffect(Unit) {
                navigateTo(ScreenRoute.CreateProfile)
            }
        }
    }
}

