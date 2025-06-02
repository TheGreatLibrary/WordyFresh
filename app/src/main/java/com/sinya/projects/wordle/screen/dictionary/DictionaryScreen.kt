package com.sinya.projects.wordle.screen.dictionary

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sinya.projects.wordle.data.local.database.AppDatabase

@Composable
fun DictionaryScreen(
    navigateToBackStack: () -> Unit,
) {
    val viewModel: DictionaryViewModel =
        viewModel(factory = DictionaryViewModel.provideFactory(
            AppDatabase.getInstance(LocalContext.current)
        )
    )

    DictionaryScreenView(
        state = viewModel.uiState.value,
        onEvent = { event -> viewModel.onEvent(event) },
        navigateToBackStack = navigateToBackStack,
        getList = viewModel.getFilteredList()
    )
}