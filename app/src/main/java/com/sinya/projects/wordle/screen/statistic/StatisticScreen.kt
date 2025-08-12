package com.sinya.projects.wordle.screen.statistic

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sinya.projects.wordle.WordyApplication
import com.sinya.projects.wordle.data.remote.supabase.sync.DictionarySync
import com.sinya.projects.wordle.data.remote.supabase.sync.StatisticSync
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.screen.dictionary.DictionaryUiEvent
import com.sinya.projects.wordle.screen.statistic.components.StatisticPlaceholder
import io.github.jan.supabase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticScreen(
    navigateToBackStack: () -> Unit,
    navigateTo: (ScreenRoute) -> Unit
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val context = LocalContext.current
    val viewModel: StatisticViewModel = viewModel(
        factory = StatisticViewModel.provideFactory(WordyApplication.database)
    )

    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            val user = WordyApplication.supabaseClient.auth.currentUserOrNull()
            if (user!=null) {
                StatisticSync.fromSupabase(context, user.id)
                viewModel.onEvent(StatisticUiEvent.Reload)
                Log.d("StatisticSync", "Синхронизация завершена!")
            }
            pullToRefreshState.endRefresh()
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .nestedScroll(pullToRefreshState.nestedScrollConnection)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(start = 16.dp, top = 50.dp, end = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            when (val state = viewModel.state.value) {
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

        PullToRefreshContainer(
            modifier = Modifier.align(Alignment.TopCenter),
            state = pullToRefreshState,
        )
    }
}