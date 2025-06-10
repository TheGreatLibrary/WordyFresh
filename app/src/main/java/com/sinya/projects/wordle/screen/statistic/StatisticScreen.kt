package com.sinya.projects.wordle.screen.statistic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sinya.projects.wordle.data.local.database.AppDatabase
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.screen.statistic.components.StatisticPlaceholder

@Composable
fun StatisticScreen(
    navigateToBackStack: () -> Unit,
    navigateTo: (ScreenRoute) -> Unit
) {
    val viewModel: StatisticViewModel = viewModel(
        factory = StatisticViewModel.provideFactory(
            AppDatabase.getInstance(LocalContext.current)
        )
    )

    val state = viewModel.state.value

    Box(
        Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 50.dp, end = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        when (state) {
            is StatisticUiState.Loading -> StatisticPlaceholder()
            is StatisticUiState.Success -> StatisticScreenView(
                uiState = state,
                onEvent = state.onEvent,
                navigateToBackStack = navigateToBackStack,
                navigateTo = navigateTo
            )
            is StatisticUiState.Error -> Text(
                text = "Ошибка: ${state.message}",
                modifier = Modifier.padding(top = 50.dp)
            )
        }
    }
}