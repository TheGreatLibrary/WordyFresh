package com.sinya.projects.wordle.screen.statistic

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sinya.projects.wordle.data.local.database.AppDatabase
import com.sinya.projects.wordle.navigation.ScreenRoute

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

    StatisticScreenView(
        uiState = viewModel.uiState.value,
        onEvent = viewModel::onEvent,
        navigateToBackStack = navigateToBackStack,
        navigateTo = navigateTo
    )
}