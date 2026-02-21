package com.sinya.projects.wordle.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.presentation.profile.subscreen.ProfileInAccount
import com.sinya.projects.wordle.presentation.profile.subscreen.ProfileOutAccount
import com.sinya.projects.wordle.presentation.profile.subscreen.ProfilePlaceholder
import com.sinya.projects.wordle.ui.features.Header

@Composable
fun ProfileScreen(
    navigateBack: () -> Unit,
    navigateTo: (ScreenRoute) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 50.dp, end = 16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Header(
            title = stringResource(R.string.profile_screen),
            trashVisible = false,
            navigateTo = navigateBack
        )

        when (state) {
            ProfileUiState.Loading -> ProfilePlaceholder()

            is ProfileUiState.Success -> ProfileInAccount(
                state = state as ProfileUiState.Success,
                onEvent = viewModel::onEvent,
                navigateTo = navigateTo,
            )

            is ProfileUiState.NoAccount -> ProfileOutAccount(navigateTo = navigateTo)
        }
    }
}

