package com.sinya.projects.wordle.presentation.statistic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.sportsdiary.presentation.error.ErrorScreen
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.presentation.statistic.components.AchievesCard
import com.sinya.projects.wordle.presentation.statistic.components.ScrollHorizontalModes
import com.sinya.projects.wordle.presentation.statistic.components.StatisticContainers
import com.sinya.projects.wordle.presentation.statistic.components.StatisticPlaceholder
import com.sinya.projects.wordle.ui.features.ScreenColumn

@Composable
fun StatisticScreen(
    navigateToBackStack: () -> Unit,
    navigateTo: (ScreenRoute) -> Unit,
    viewModel: StatisticViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        StatisticUiState.Loading -> StatisticPlaceholder(
            title = stringResource(R.string.statistic_screen),
            navigateToBackStack = navigateToBackStack
        )

        is StatisticUiState.Success -> StatisticScreenView(
            state = state as StatisticUiState.Success,
            onEvent = viewModel::onEvent,
            navigateToBackStack = navigateToBackStack,
            navigateTo = navigateTo
        )

        is StatisticUiState.Error -> ErrorScreen((state as StatisticUiState.Error).errorMessage)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatisticScreenView(
    state: StatisticUiState.Success,
    onEvent: (StatisticEvent) -> Unit,
    navigateToBackStack: () -> Unit,
    navigateTo: (ScreenRoute) -> Unit
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val snackbarHostState = remember { SnackbarHostState() }
    val errorText = state.errorMessage?.let { stringResource(it) }

    LaunchedEffect(state.errorMessage, state.isRefreshing) {
        errorText?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            onEvent(StatisticEvent.OnErrorShown)
        }

        if (!state.isRefreshing) {
            pullToRefreshState.endRefresh()
        }
    }

    if (pullToRefreshState.isRefreshing && !state.isRefreshing) {
        LaunchedEffect(Unit) {
            onEvent(StatisticEvent.OnRefresh)
        }
    }

    Box(Modifier.nestedScroll(pullToRefreshState.nestedScrollConnection)) {
        ScreenColumn(
            title = stringResource(R.string.statistic_screen),
            trashVisible = true,
            navigateBack = navigateToBackStack,
            onTrashClick = { onEvent(StatisticEvent.OnClearAll) }
        ) {
            ScrollHorizontalModes(
                selectedMode = state.selectedMode,
                modes = state.modes,
                onModeSelect = { mode ->
                    onEvent(StatisticEvent.SelectMode(mode))
                }
            )

            StatisticContainers(
                statisticByMode = state.currentStatistic
            )

            AchievesCard(navigateTo)

            Spacer(Modifier.height(18.dp))
        }

        PullToRefreshContainer(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .alpha(if (pullToRefreshState.isRefreshing) 1f else 0f),
            state = pullToRefreshState,
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}
