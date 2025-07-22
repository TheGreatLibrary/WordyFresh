package com.sinya.projects.wordle.screen.edit

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
import com.sinya.projects.wordle.screen.edit.components.EditForm
import com.sinya.projects.wordle.screen.resetPassword.components.ResetForm
import com.sinya.projects.wordle.ui.features.AuthHeader
import com.sinya.projects.wordle.ui.features.Header

@Composable
fun EditScreenView(
    navigateToBackStack: () -> Unit,
    state: EditUiState,
    onEvent: (EditUiEvent) -> Unit,
    modifier: Modifier,
    onReset: () -> Unit
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
            title = stringResource(R.string.edit),
            subtitle = stringResource(R.string.put_new_nickname),
        )
        EditForm(
            state = state,
            onEvent = onEvent,
            modifier = modifier,
            onReset = onReset
        )
    }
}