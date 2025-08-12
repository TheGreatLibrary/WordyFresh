package com.sinya.projects.wordle.screen.resetEmail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.screen.resetEmail.components.ResetForm
import com.sinya.projects.wordle.ui.features.AuthHeader
import com.sinya.projects.wordle.ui.features.Header

@Composable
fun ResetEmailScreenView(
    navigateToBackStack: () -> Unit,
    state: ResetEmailUiState.ResetForm,
    onEvent: (ResetEmailUiEvent) -> Unit,
    modifier: Modifier,
    onReset: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(27.dp)
    ) {
        Header(
            title = "",
            trashVisible = false,
            navigateTo = navigateToBackStack
        )
        AuthHeader(
            title = stringResource(R.string.reset_email),
            subtitle = stringResource(R.string.put_new_email),
        )
        ResetForm(
            state = state,
            onEvent = onEvent,
            modifier = modifier,
            onReset = onReset
        )
    }
}