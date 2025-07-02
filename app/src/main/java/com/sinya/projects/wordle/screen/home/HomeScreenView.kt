package com.sinya.projects.wordle.screen.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.screen.home.components.BottomSheet
import com.sinya.projects.wordle.screen.home.components.MainContainers
import com.sinya.projects.wordle.screen.home.components.MainHeader
import com.sinya.projects.wordle.screen.home.friend_dialog.FriendModeDialog
import com.sinya.projects.wordle.ui.features.RoundedButton
import com.sinya.projects.wordle.ui.theme.Montserrat
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography
import java.util.concurrent.TimeUnit

@SuppressLint("DefaultLocale")
@Composable
fun HomeScreenView(
    state: HomeUiState.Success,
    navigateTo: (ScreenRoute) -> Unit,
    sendEmail: () -> Unit,
    onEvent: (HomeUiEvent) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize(),
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
                    onEvent(HomeUiEvent.FriendDialogUploadVisible(true))
                },
                onHardClick = {
                    onEvent(HomeUiEvent.BottomSheetUploadMode(1))
                },
                onRandomClick = { navigateTo(ScreenRoute.Game(mode = 3)) },
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (state.savedGame != null) {
                RoundedButton(
                    modifier = Modifier.fillMaxWidth(0.7f),
                    colors = ButtonDefaults.buttonColors(containerColor = WordyColor.colors.backgroundActiveBtnMkI),
                    contentPadding = PaddingValues(vertical = 5.dp, horizontal = 15.dp),
                    onClick = {
                        val savedGame = state.savedGame
                        navigateTo(ScreenRoute.Game(
                            mode = -1,
                            wordLength = savedGame.length,
                            lang = savedGame.lang,
                            word = savedGame.targetWord
                        ))
                    }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            stringResource(R.string.continue_text),
                            fontSize = 16.sp,
                            color = WordyColor.colors.textForActiveBtnMkI,
                            style = TextStyle(
                                lineHeight = 16.sp,
                                fontFamily = Montserrat,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Text(
                            "${
                                state.savedGame.length.let {
                                    LocalContext.current.resources.getQuantityString(
                                        R.plurals.letters_count,
                                        it, state.savedGame.length
                                    )
                                }
                            } - ${
                                String.format(
                                    "%02d:%02d",
                                    TimeUnit.SECONDS.toMinutes(state.savedGame?.totalSeconds ?: 0L)
                                        .toInt(),
                                    (state.savedGame.totalSeconds ?: 0L) % 60
                                )
                            }",
                            fontSize = 12.sp,
                            color = WordyColor.colors.textForActiveBtnMkI,
                            style = TextStyle(
                                lineHeight = 12.sp,
                                fontFamily = Montserrat,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
            RoundedButton(
                modifier = Modifier.fillMaxWidth(0.7f),
                colors = ButtonDefaults.buttonColors(containerColor = WordyColor.colors.backgroundActiveBtnMkII),
                contentPadding = PaddingValues(vertical = 3.dp, horizontal = 15.dp),
                onClick = { onEvent(HomeUiEvent.BottomSheetUploadMode(0))
                }
            ) {
                Text(
                    stringResource(R.string.new_game),
                    fontSize = 16.sp,
                    color = WordyColor.colors.textForActiveBtnMkII,
                    style = WordyTypography.bodyMedium
                )
            }
        }
    }

    if (state.showFriendDialog) {
        FriendModeDialog(
            navigateTo,
            onDismiss = { onEvent(HomeUiEvent.FriendDialogUploadVisible(false)) }
        )
    }

    if (state.showBottomSheet) {
        BottomSheet(
            onClickGame = { route ->
                navigateTo(route)
            },
            onDismissRequest = { onEvent(HomeUiEvent.BottomSheetUploadVisible(false)) },
            mode = state.modeGame
        )
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreenView(
        state = HomeUiState.Success(
            onEvent = { }
        ),
        navigateTo = {  },
        sendEmail = {  },
        onEvent = { }
    )
}