package com.sinya.projects.wordle.screen.dictionary

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.domain.model.data.DictionaryItem
import com.sinya.projects.wordle.screen.dictionary.components.DictionaryCard
import com.sinya.projects.wordle.screen.dictionary.components.SearchContainer
import com.sinya.projects.wordle.ui.features.Header
import kotlinx.coroutines.delay

@SuppressLint("UseOfNonLambdaOffsetOverload")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionaryScreenView(
    state: DictionaryUi,
    onEvent: (DictionaryUiEvent) -> Unit,
    navigateToBackStack: () -> Unit,
    getList: List<DictionaryItem>
) {
    val context = LocalContext.current
    val pullToRefreshState = rememberPullToRefreshState()
    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            delay(1000)
            pullToRefreshState.endRefresh()
        }
    }

    val listState = rememberLazyListState()
    val showHeader by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset < 350
        }
    }

    val offsetY by animateDpAsState(
        targetValue = if (showHeader) 0.dp else (-100).dp,
        label = "Header animation"
    )
    val alpha by animateFloatAsState(
        targetValue = if (showHeader) 1f else 0f,
        label = "Header alpha"
    )

    Box(Modifier.nestedScroll(pullToRefreshState.nestedScrollConnection)) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .offset(y = offsetY)
                        .alpha(alpha)
                        .padding(top = 50.dp)
                ) {
                    Header(
                        title = stringResource(R.string.dictionary),
                        trashVisible = false,
                        navigateTo = navigateToBackStack)
                    SearchContainer(
                        searchQuery = state.searchQuery,
                        onValueChanged = { query -> onEvent(DictionaryUiEvent.OnSearchQueryChanged(query)) }
                    )
                    Spacer(Modifier.height(21.dp))
                }
            }

            itemsIndexed(getList, key = { _, item -> item.word }) { _, word ->
                DictionaryCard(
                    context = context,
                    title = word.word,
                    description = word.description,
                    onEvent = onEvent
                )
            }

            item {
                Spacer(modifier = Modifier.height(7.dp))
            }
        }

        PullToRefreshContainer(
            modifier = Modifier.align(Alignment.TopCenter),
            state = pullToRefreshState,
        )
    }
}

@Preview
@Composable
private fun DictionaryViewPreview() {
    DictionaryScreenView(
        navigateToBackStack = { },
        onEvent = { },
        state = DictionaryUi(),
        getList = emptyList()
    )
}