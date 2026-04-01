package com.sinya.projects.wordle.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.wordle.domain.enums.GameMode
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.presentation.home.components.ContinueGameButton
import com.sinya.projects.wordle.presentation.home.components.HomePlaceholder
import com.sinya.projects.wordle.presentation.home.components.MainContainers
import com.sinya.projects.wordle.presentation.home.components.MainHeader
import com.sinya.projects.wordle.presentation.home.components.NewGameBottomSheet
import com.sinya.projects.wordle.presentation.home.components.NewGameButton
import com.sinya.projects.wordle.presentation.home.friendSheet.FriendSheet
import com.sinya.projects.wordle.utils.findActivity
import com.sinya.projects.wordle.utils.sendSupportEmail

@Composable
fun HomeScreen(
    navigateTo: (ScreenRoute) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    val activity = context.findActivity()
    LaunchedEffect(Unit) {
        val deepLinkUri = activity?.intent
        viewModel.handleDeepLink(deepLinkUri)
        activity?.intent?.data = null
    }

    Box(
        Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(start = 16.dp, end = 16.dp, bottom = 50.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        when (state) {
            HomeUiState.Loading -> HomePlaceholder()

            is HomeUiState.Success -> HomeScreenView(
                state = state as HomeUiState.Success,
                navigateTo = navigateTo,
                sendEmail = {
                    context.sendSupportEmail()
                    viewModel.onEvent(HomeEvent.SendEmailSupport)
                },
                onEvent = viewModel::onEvent
            )

            is HomeUiState.Invite -> {
                LaunchedEffect(Unit) {
                    navigateTo((state as HomeUiState.Invite).game)
                }
            }
        }
    }
}

@Composable
private fun HomeScreenView(
    state: HomeUiState.Success,
    navigateTo: (ScreenRoute) -> Unit,
    sendEmail: () -> Unit,
    onEvent: (HomeEvent) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val errorText = state.errorMessage?.let { stringResource(it) }

    LaunchedEffect(state.errorMessage) {
        errorText?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            onEvent(HomeEvent.OnErrorShown)
        }
    }

    Box(modifier = Modifier.widthIn(max = 550.dp)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column {
                MainHeader(
                    avatarUri = state.avatarUri,
                    onAvatarClick = { navigateTo(ScreenRoute.Profile) },
                    onEmailClick = sendEmail
                )
                MainContainers(
                    onFriendClick = {
                        onEvent(HomeEvent.FriendDialogUploadVisible(true))
                    },
                    onHardClick = {
                        onEvent(HomeEvent.BottomSheetUploadMode(mode = GameMode.HARD))
                    },
                    onRandomClick = {
                        onEvent(HomeEvent.BottomSheetUploadMode(mode = GameMode.RANDOM))
                    },
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(9.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                state.savedGame?.let {
                    ContinueGameButton(
                        savedGame = it,
                        onClick = {
                            navigateTo(
                                ScreenRoute.Game(
                                    mode = GameMode.SAVED.id,
                                    wordLength = it.length,
                                    lang = it.lang,
                                    word = it.targetWord
                                )
                            )
                        }
                    )
                }
                NewGameButton(
                    onClick = { onEvent(HomeEvent.BottomSheetUploadMode(GameMode.NORMAL)) }
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }

    if (state.showFriendBottomSheet) {
        FriendSheet(
            navigateTo = navigateTo,
            onDismissRequest = { onEvent(HomeEvent.FriendDialogUploadVisible(false)) }
        )
    }

    if (state.showGameBottomSheet) {
        NewGameBottomSheet(
            navigateTo = navigateTo,
            onDismissRequest = { onEvent(HomeEvent.BottomSheetUploadVisible(false)) },
            initialMode = state.modeGame
        )
    }
}
