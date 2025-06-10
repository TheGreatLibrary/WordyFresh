package com.sinya.projects.wordle.screen.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.screen.register.components.RegisterForm
import com.sinya.projects.wordle.ui.features.AuthFooter
import com.sinya.projects.wordle.ui.features.AuthHeader
import com.sinya.projects.wordle.ui.features.Header
import com.sinya.projects.wordle.ui.theme.white


@Composable
fun RegisterScreenView(
    state: RegisterUiState,
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
    ) {
        Header(
            title = "",
            trashVisible = false,
            navigateTo = navigateToBackStack
        )
        Spacer(Modifier.height(35.dp))
        AuthHeader(
            title = stringResource(R.string.create_account),
            subtitle = stringResource(R.string.put_string_and_play)
        )
        Spacer(Modifier.height(35.dp))
        RegisterForm(
            state = state,
            onEvent = onEvent,
            navigateTo = { navigateTo(ScreenRoute.Login)},
            modifier = modifier,
            onRegisterIn = onRegisterIn
        )
        Spacer(Modifier.height(35.dp))
        AuthFooter(
            title = stringResource(R.string.or_sign_up_with),
            titleFooter = stringResource(R.string.already_have_account),
            textLink = stringResource(R.string.sing_in_1),
            navigateTo = { navigateTo(ScreenRoute.Login)}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterScreenPreview() {
    RegisterScreenView(
        state = RegisterUiState(),
        onEvent = {   },
        modifier = Modifier
            .fillMaxWidth()
            .background(white, RoundedCornerShape(100))
            .padding(horizontal = 32.dp, vertical = 16.dp),
        navigateToBackStack = { },
        navigateTo = { },
        onRegisterIn = { },
    )
}