package com.sinya.projects.wordle.screen.statistic

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.WordyApplication
import com.sinya.projects.wordle.data.remote.supabase.SupabaseService
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.screen.statistic.components.AchievesCard
import com.sinya.projects.wordle.screen.statistic.components.ScrollHorizontalModes
import com.sinya.projects.wordle.screen.statistic.components.StatContainers
import com.sinya.projects.wordle.ui.features.Header
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

@Composable
fun StatisticScreenView(
    uiState: StatisticUiState.Success,
    onEvent: (StatisticUiEvent) -> Unit,
    navigateToBackStack: () -> Unit,
    navigateTo: (ScreenRoute) -> Unit
) {
    val coroutine = rememberCoroutineScope()
    Column {
        Header(
            title = stringResource(R.string.statistic_screen),
            trashVisible = true,
            navigateTo = navigateToBackStack,
            trashOnClick = {
                coroutine.launch {
                    val user =  WordyApplication.supabaseClient.auth.currentUserOrNull()
                    WordyApplication.database.clearAllStatistic()
                    if (user!=null) {
                        SupabaseService.clearAllUserData(user.id)
                    }
                    else Log.d("SupabaseDelete", "Ошибка - пользователь не активировал сессию")
                }
            }
        )
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