package com.sinya.projects.wordle.screen.dictionary

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sinya.projects.wordle.data.local.database.AppDatabase
import com.sinya.projects.wordle.screen.dictionary.components.DictionaryPlaceholder

@Composable
fun DictionaryScreen(
    navigateToBackStack: () -> Unit,
) {
    val viewModel: DictionaryViewModel =
        viewModel(factory = DictionaryViewModel.provideFactory(
            AppDatabase.getInstance(LocalContext.current)
        )
    )

    val state = viewModel.state.value

    Crossfade(targetState = state) { state ->
        when(state) {
            is DictionaryUiState.Loading -> DictionaryPlaceholder()
            is DictionaryUiState.Success -> DictionaryScreenView(
                state = state,
                onEvent = state.onEvent,
                navigateToBackStack = navigateToBackStack,
                getList = viewModel.getFilteredList(state)
            )
            is DictionaryUiState.Error -> Text(
                text = "Ошибка: ${state.message}",
                modifier = Modifier.padding(top = 50.dp)
            )
        }
    }
}