package com.sinya.projects.wordle.screen.register.subscreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.screen.register.RegisterUiEvent
import com.sinya.projects.wordle.screen.register.RegisterUiState
import com.sinya.projects.wordle.screen.register.components.RegisterForm
import com.sinya.projects.wordle.ui.features.AuthHeader
import com.sinya.projects.wordle.ui.features.Header
import com.sinya.projects.wordle.ui.features.RowVariableAuth

@Composable
fun RegisterForm(
    state: RegisterUiState.RegisterForm,
    onEvent: (RegisterUiEvent) -> Unit,
    modifier: Modifier,
    navigateToBackStack: () -> Unit,
    navigateTo: (ScreenRoute) -> Unit,
    onRegisterIn: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 8.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(27.dp)
    ) {
        Header(
            title = "",
            trashVisible = false,
            navigateTo = navigateToBackStack
        )
        AuthHeader(
            title = stringResource(R.string.create_account),
            subtitle = stringResource(R.string.put_string_and_play)
        )
        RegisterForm(
            state = state,
            onEvent = onEvent,
            modifier = modifier,
            onRegisterIn = onRegisterIn
        )
        RowVariableAuth(
            title = stringResource(R.string.already_have_account),
            text = stringResource(R.string.sing_in_1),
            navigateTo = { navigateTo(ScreenRoute.Login)}
        )
    }
}