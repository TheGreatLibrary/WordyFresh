package com.sinya.projects.wordle.screen.statistic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.screen.statistic.components.AchievesCard
import com.sinya.projects.wordle.screen.statistic.components.ScrollHorizontalModes
import com.sinya.projects.wordle.screen.statistic.components.StatContainers
import com.sinya.projects.wordle.ui.features.Header

@Composable
fun StatisticScreenView(
    uiState: StatisticUiState.Success,
    onEvent: (StatisticUiEvent) -> Unit,
    navigateToBackStack: () -> Unit,
    navigateTo: (ScreenRoute) -> Unit
) {
    Column {
        Header(stringResource(R.string.statistic_screen), true, navigateToBackStack)
        ScrollHorizontalModes(uiState, onEvent)
        StatContainers(uiState)
        AchievesCard(navigateTo)
        Spacer(Modifier.height(18.dp))
    }
}

@Preview
@Composable
private fun StatisticScreenPreview() {
    StatisticScreenView(
        uiState = StatisticUiState.Success(
            onEvent = {}
        ),
        onEvent = {},
        navigateToBackStack = { },
        navigateTo = { }
    )
}