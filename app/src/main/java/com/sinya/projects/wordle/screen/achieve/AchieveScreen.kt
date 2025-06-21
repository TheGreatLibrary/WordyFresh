package com.sinya.projects.wordle.screen.achieve

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sinya.projects.wordle.WordyApplication
import com.sinya.projects.wordle.data.local.database.AppDatabase
import com.sinya.projects.wordle.screen.dictionary.components.DictionaryPlaceholder

@Composable
fun AchieveScreen(
    navigateToBackStack: () -> Unit,
) {
    val db = WordyApplication.database

    val viewModel: AchieveViewModel =
        viewModel(
            factory = AchieveViewModel.provideFactory(
                db
//                AppDatabase.getInstance(LocalContext.current)
            )
        )

    val state = viewModel.state.value

    Crossfade(targetState = state) { state ->
        when (state) {
            is AchieveUiState.Loading -> DictionaryPlaceholder()
            is AchieveUiState.Success -> AchieveScreenView(
                state = state,
                onEvent = state.onEvent,
                navigateToBackStack = navigateToBackStack
            )
            is AchieveUiState.Error -> Text(
                text = "Ошибка: ${state.message}",
                modifier = Modifier.padding(top = 50.dp)
            )
        }
    }
}