package com.sinya.projects.wordle.presentation.achieve

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.presentation.achieve.components.AchieveDialog
import com.sinya.projects.wordle.presentation.achieve.components.AchievePlaceholder
import com.sinya.projects.wordle.presentation.achieve.components.AchieveRow
import com.sinya.projects.wordle.presentation.achieve.components.CategoryHeader
import com.sinya.projects.wordle.ui.features.Header

@Composable
fun AchieveScreen(
    id: Int?,
    navigateToBackStack: () -> Unit
) {
    val viewModel: AchieveViewModel = hiltViewModel(
        creationCallback = { factory: AchieveViewModel.Factory ->
            factory.create(
                id = id
            )
        }
    )

    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        AchieveUiState.Loading -> AchievePlaceholder(
            title = stringResource(R.string.achievements),
            navigateToBackStack = navigateToBackStack,
        )

        is AchieveUiState.Success -> AchieveScreenView(
            state = state as AchieveUiState.Success,
            onEvent = viewModel::onEvent,
            navigateBack = navigateToBackStack
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AchieveScreenView(
    state: AchieveUiState.Success,
    onEvent: (AchieveEvent) -> Unit,
    navigateBack: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val pullToRefreshState = rememberPullToRefreshState()
    val errorText = state.errorMessage?.let { stringResource(it) }

    LaunchedEffect(state.isRefreshing, state.errorMessage) {
        errorText?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            onEvent(AchieveEvent.OnErrorShown)
        }

        if (!state.isRefreshing) {
            pullToRefreshState.endRefresh()
        }
    }

    if (pullToRefreshState.isRefreshing && !state.isRefreshing) {
        LaunchedEffect(Unit) {
            onEvent(AchieveEvent.OnRefresh)
        }
    }

    Box(Modifier.nestedScroll(pullToRefreshState.nestedScrollConnection)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            item {
                Column {
                    Header(
                        title = stringResource(R.string.achievements),
                        navigateTo = navigateBack,
                        trashVisible = true,
                        trashOnClick = { onEvent(AchieveEvent.OnClearAll) }
                    )
                    Spacer(Modifier.height(18.dp))
                }
            }

            state.achieveList.forEach { (category, items) ->
                item(key = category) {
                    CategoryHeader(
                        categoryName = category,
                        finishedCount = items.count { it.count >= it.maxCount },
                        totalCount = items.size
                    )
                }

                val chunkedItems = items.chunked(3)
                chunkedItems.forEachIndexed { rowIndex, row ->
                    item(key = "${category}_row_$rowIndex") {
                        AchieveRow(
                            items = row,
                            onEvent = onEvent,
                            modifier = Modifier.padding(bottom = 9.dp)
                        )
                    }
                }

                item(key = "spacer_$category") {
                    Spacer(Modifier)
                }
            }
        }

        if (state.showAchieveDialog!=null) {
            AchieveDialog(
                achieveItem = state.showAchieveDialog,
                onDismiss = { onEvent(AchieveEvent.VisibleDialog(null)) }
            )
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