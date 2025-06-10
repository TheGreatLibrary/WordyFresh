package com.sinya.projects.wordle.screen.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.sinya.projects.wordle.navigation.ScreenRoute


@Composable
fun ProfileInAccount(
    state: ProfileUiState.Success,
    navigateTo: (ScreenRoute) -> Unit,
) {
    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        uri -> uri?.let {
            state.onEvent(ProfileUiEvent.UpdateAvatar(it))
        }
    }

    LaunchedEffect(Unit) {
        state.onEvent(ProfileUiEvent.LoadAvatar)
    }

    ProfileInAccountView(
        onPickClicked = { pickImageLauncher.launch("image/*") },
        state = state,
        navigateTo = navigateTo,
    )
}