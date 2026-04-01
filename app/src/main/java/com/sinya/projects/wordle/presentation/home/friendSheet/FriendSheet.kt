package com.sinya.projects.wordle.presentation.home.friendSheet

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.data.remote.web.LegalLinks
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.presentation.home.friendSheet.components.TabContent
import com.sinya.projects.wordle.presentation.home.friendSheet.components.TabHeader
import com.sinya.projects.wordle.ui.features.CustomModalSheet
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyShapes
import com.sinya.projects.wordle.ui.theme.WordyTypography
import com.sinya.projects.wordle.ui.theme.white

@Composable
fun FriendSheet(
    navigateTo: (ScreenRoute) -> Unit,
    onDismissRequest: () -> Unit,
    viewModel: FriendViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.copyRequest) {
        viewModel.copyRequest.collect { cipher ->
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Шифр слова", cipher)
            clipboard.setPrimaryClip(clip)

            snackbarHostState.showSnackbar(
                message = context.getString(R.string.cipher_copied),
                duration = SnackbarDuration.Short
            )
        }
    }

    when(state) {
        is FriendUiState.FriendForm -> {
            Box {
                FriendModalForm(
                    onDismissRequest = onDismissRequest,
                    state = state as FriendUiState.FriendForm,
                    onEvent = viewModel::onEvent
                )

                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                )
            }
        }

        is FriendUiState.Success -> {
            LaunchedEffect(Unit) {
                navigateTo((state as FriendUiState.Success).game)
                viewModel.onEvent(FriendEvent.ClearState)
            }
        }
    }
}

@Composable
private fun FriendModalForm(
    onDismissRequest: () -> Unit,
    state: FriendUiState.FriendForm,
    onEvent: (FriendEvent) -> Unit,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .background(white, WordyShapes.large)
        .padding(horizontal = 12.dp, vertical = 10.dp)
) {
    val context = LocalContext.current

    val onShare: (String) -> Unit = remember {
        { url ->
            val text = context.getString(
                R.string.share_invite,
                LegalLinks.formatInviteUrl(url)
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            context.startActivity(
                Intent.createChooser(intent, context.getString(R.string.shared_to))
            )
        }
    }

    LaunchedEffect(state.inviteWord) {
        state.inviteWord?.let {
            onShare(state.inviteWord)
            onEvent(FriendEvent.InviteCipherShown)
        }
    }

    CustomModalSheet(onDismissRequest) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp, bottom = 45.dp, start = 24.dp, end = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = stringResource(R.string.friend_mode),
                color = WordyColor.colors.textPrimary,
                style = WordyTypography.titleLarge,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            TabRow(
                selectedTabIndex = state.selectedTab,
                containerColor = Color.Transparent,
                contentColor = WordyColor.colors.textPrimary,
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[state.selectedTab])
                            .height(2.dp),
                        color = WordyColor.colors.backPrimary
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                TabHeader(
                    selected = state.selectedTab == 0,
                    onClick = { onEvent(FriendEvent.OnTabClick(0)) },
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.get_shifr_word)
                )
                TabHeader(
                    selected = state.selectedTab == 1,
                    onClick = { onEvent(FriendEvent.OnTabClick(1)) },
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.put_shifr_word)
                )
            }

            when (state.selectedTab) {
                0 -> TabContent(
                    description = stringResource(R.string.enter_word_to_encode),
                    placeholder = stringResource(R.string.put_here),
                    errorMessage = stringResource(R.string.is_word_in_database_error),
                    textButton = stringResource(R.string.get_cipher),
                    isLoading = state.isLoading,
                    value = state.hiddenPlace,
                    isError = state.isError,
                    onValueChange = { onEvent(FriendEvent.OnHiddenPlaceChange(it)) },
                    onClick = { onEvent(FriendEvent.EncodeCipher) },
                    onShareClick = { onEvent(FriendEvent.EncodeCipherToShare) },
                    modifier = modifier
                )

                1 -> TabContent(
                    description = stringResource(R.string.enter_cipher_to_decode),
                    placeholder = stringResource(R.string.cipher_placeholder),
                    errorMessage = stringResource(R.string.invalid_cipher_error),
                    textButton = stringResource(R.string.start_game),
                    value = state.guessedPlace,
                    isLoading = state.isLoading,
                    isError = state.isError,
                    onValueChange = { onEvent(FriendEvent.OnGuessedPlaceChange(it)) },
                    onClick = { onEvent(FriendEvent.DecodeCipher) },
                    modifier = modifier
                )
            }
        }
    }
}

