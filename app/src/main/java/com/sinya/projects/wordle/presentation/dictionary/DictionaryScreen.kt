package com.sinya.projects.wordle.presentation.dictionary

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.data.remote.web.LegalLinks
import com.sinya.projects.wordle.domain.model.DictionaryItem
import com.sinya.projects.wordle.presentation.dictionary.components.DictionaryCard
import com.sinya.projects.wordle.presentation.dictionary.components.DictionaryHeader
import com.sinya.projects.wordle.presentation.dictionary.components.DictionaryPlaceholder
import com.sinya.projects.wordle.utils.openUrl
import com.sinya.projects.wordle.utils.shareDescription

@Composable
fun DictionaryScreen(
    navigateToBackStack: () -> Unit,
    viewModel: DictionaryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val filteredList by viewModel.filteredList.collectAsStateWithLifecycle()

    when (state) {
        DictionaryUiState.Loading -> DictionaryPlaceholder(
            title = stringResource(R.string.dictionary),
            navigateToBackStack = navigateToBackStack,
        )

        is DictionaryUiState.Success -> DictionaryScreenView(
            state = state as DictionaryUiState.Success,
            onEvent = viewModel::onEvent,
            navigateToBackStack = navigateToBackStack,
            filtredList = filteredList
        )
    }
}

@Composable
private fun DictionaryScreenView(
    state: DictionaryUiState.Success,
    onEvent: (DictionaryEvent) -> Unit,
    navigateToBackStack: () -> Unit,
    filtredList: List<DictionaryItem>,
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    val onOpenUrl: (String) -> Unit = remember {
        { word -> context.openUrl(LegalLinks.formatAcademicUrl(word)) }
    }
    val onShare: (String, String) -> Unit = remember {
        { word, description -> context.shareDescription(word, description) }
    }

    val pullToRefreshState = rememberPullToRefreshState()
    val snackbarHostState = remember { SnackbarHostState() }
    val errorText = state.errorMessage?.let { stringResource(it) }

    val listState = rememberLazyListState()

    LaunchedEffect(state.errorMessage) {
        errorText?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            onEvent(DictionaryEvent.OnErrorShown)
        }
    }

    var headerHeightPx by remember { mutableFloatStateOf(0f) }
    var headerOffsetPx by remember { mutableFloatStateOf(0f) }

    val headerScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = headerOffsetPx + delta

                if (headerHeightPx > 0f) {
                    headerOffsetPx = newOffset.coerceIn(-headerHeightPx, 0f)
                }

                return Offset.Zero
            }
        }
    }

    if (listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0) {
        headerOffsetPx = 0f
    }

    Box(modifier = Modifier.fillMaxSize()) {
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { onEvent(DictionaryEvent.OnRefresh) },
            state = pullToRefreshState,
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 550.dp)
                .windowInsetsPadding(WindowInsets.statusBars)
                .nestedScroll(headerScrollConnection),
            contentAlignment = Alignment.TopCenter
        ) {
            val headerHeightDp = with(density) { headerHeightPx.toDp() }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxHeight(),
                contentPadding = PaddingValues(top = headerHeightDp + 8.dp)
            ) {
                items(
                    items = filtredList,
                    key = { it.word }
                ) { word ->
                    DictionaryCard(
                        onOpenUrl = onOpenUrl,
                        onShare = onShare,
                        item = word,
                        onEvent = onEvent
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(18.dp))
                }
            }

            DictionaryHeader(
                modifier = Modifier
                    .onSizeChanged { size ->
                        headerHeightPx = size.height.toFloat()
                    }
                    .graphicsLayer {
                        translationY = headerOffsetPx

                        if (headerHeightPx > 0f) {
                            alpha = 1f - (-headerOffsetPx / headerHeightPx)
                        }
                    },
                navigateToBackStack = navigateToBackStack,
                onEvent = onEvent,
                state = state
            )

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}