package com.sinya.projects.wordle.screen.achieve

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.WordyApplication
import com.sinya.projects.wordle.data.local.database.AppDatabase
import com.sinya.projects.wordle.data.supabase.SupabaseService
import com.sinya.projects.wordle.screen.achieve.components.AchievesBlock
import com.sinya.projects.wordle.ui.features.Header
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

@SuppressLint("UseOfNonLambdaOffsetOverload")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchieveScreenView(
    state: AchieveUiState.Success,
    onEvent: (AchieveUiEvent) -> Unit,
    navigateToBackStack: () -> Unit,
) {
    val coroutine = rememberCoroutineScope()
    val pullToRefreshState = rememberPullToRefreshState()
    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            onEvent(AchieveUiEvent.OnRefreshList)
            pullToRefreshState.endRefresh()
        }
    }

    Box(Modifier.nestedScroll(pullToRefreshState.nestedScrollConnection)) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            item {
                Column(modifier = Modifier.padding(top = 50.dp)) {
                    Header(
                        title = stringResource(R.string.achievements),
                        navigateTo = navigateToBackStack,
                        trashVisible = true,
                        trashOnClick = {
                           coroutine.launch {
                               val user =  WordyApplication.supabaseClient.auth.currentUserOrNull()
                               WordyApplication.database.clearAll()
                               if (user!=null) {
                                   SupabaseService.clearAllUserData(user.id)
                               }
                               else Log.d("SupabaseDelete", "Ошибка - пользователь не активировал сессию")
                           }
                        }
                    )
                    Spacer(Modifier.height(21.dp))
                }
            }

            val groupedAchieves = state.achieveList.groupBy { it.categoryName }

            groupedAchieves.forEach { (_, items) ->
                item {
                    AchievesBlock(
                        achieveItems = items
                    )
                }
            }
        }

        PullToRefreshContainer(
            modifier = Modifier.align(Alignment.TopCenter),
            state = pullToRefreshState,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AchieveScreenPreview() {
    AchieveScreenView(
        navigateToBackStack = { },
        onEvent = { },
        state = AchieveUiState.Success(
            onEvent = { }
        ),
    )
}
