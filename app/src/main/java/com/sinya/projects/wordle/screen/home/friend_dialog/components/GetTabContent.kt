package com.sinya.projects.wordle.screen.home.friend_dialog.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.screen.home.friend_dialog.FriendModeUiEvent
import com.sinya.projects.wordle.screen.home.friend_dialog.FriendModeUiState
import com.sinya.projects.wordle.ui.theme.WordyShapes
import com.sinya.projects.wordle.ui.theme.white


@Composable
fun GetTabContent(
    navigateTo: (ScreenRoute) -> Unit,
    state: FriendModeUiState,
    onEvent: (FriendModeUiEvent) -> Unit,
) {
    val modifier = Modifier
        .fillMaxWidth()
        .background(white, WordyShapes.large)
        .padding(horizontal = 12.dp, vertical = 10.dp)

    when (state.selectedTab) {
        0 -> WordInputSection(
            title = stringResource(R.string.put_word),
            textFieldValue = state.hiddenPlace,
            onTextFieldChange = { onEvent(FriendModeUiEvent.OnHiddenPlaceChange(it)) },
            buttonText = stringResource(R.string.copy_shifr),
            modifier = modifier,
            onButtonClick = { onEvent(FriendModeUiEvent.EncodeCipher) },
            errorText = state.errorText,
            isError = state.isError
        )

        1 -> WordInputSection(
            title = stringResource(R.string.put_shifr),
            textFieldValue = state.guessedPlace,
            onTextFieldChange = { onEvent(FriendModeUiEvent.OnGuessedPlaceChange(it)) },
            buttonText = stringResource(R.string.decode_word),
            modifier = modifier,
            onButtonClick = { onEvent(FriendModeUiEvent.DecodeCipher(navigateTo = navigateTo)) },
            errorText = state.errorText,
            isError = state.isError
        )
    }
}