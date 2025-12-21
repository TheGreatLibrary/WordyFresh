package com.sinya.projects.wordle.presentation.dictionary

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.data.remote.web.LegalLinks
import com.sinya.projects.wordle.domain.model.DictionaryItem
import com.sinya.projects.wordle.presentation.dictionary.components.DictionaryCard
import com.sinya.projects.wordle.presentation.dictionary.components.DictionaryPlaceholder
import com.sinya.projects.wordle.presentation.dictionary.components.SearchContainer
import com.sinya.projects.wordle.ui.features.Header

@Composable
fun DictionaryScreen(
    navigateToBackStack: () -> Unit,
    viewModel: DictionaryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val filteredList by viewModel.filteredList.collectAsStateWithLifecycle()

    when (state) {
        DictionaryUiState.Loading -> DictionaryPlaceholder()

        is DictionaryUiState.Success -> DictionaryScreenView(
            state = state as DictionaryUiState.Success,
            onEvent = viewModel::onEvent,
            navigateToBackStack = navigateToBackStack,
            filtredList = filteredList
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DictionaryScreenView(
    state: DictionaryUiState.Success,
    onEvent: (DictionaryEvent) -> Unit,
    navigateToBackStack: () -> Unit,
    filtredList: List<DictionaryItem>
) {
    val context = LocalContext.current

    val onOpenUrl: (String) -> Unit = remember {
        { word ->
            val url = LegalLinks.formatAcademicUrl(word)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }
    }

    val onShare: (String, String) -> Unit = remember {
        { word, description ->
            val text = context.getString(
                R.string.share_text,
                word,
                description.ifEmpty { "" },
                LegalLinks.WORDY_APP_URL
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            context.startActivity(
                Intent.createChooser(intent, context.getString(R.string.shared_to))
            )
        }
    }

    val pullToRefreshState = rememberPullToRefreshState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage, state.isRefreshing) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            onEvent(DictionaryEvent.OnErrorShown)
        }

        if (!state.isRefreshing) {
            pullToRefreshState.endRefresh()
        }
    }

    if (pullToRefreshState.isRefreshing && !state.isRefreshing) {
        LaunchedEffect(Unit) {
            onEvent(DictionaryEvent.OnRefresh)
        }
    }

    val listState = rememberLazyListState()

    val showHeader by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 &&
            listState.firstVisibleItemScrollOffset < 350
        }
    }

    Box(Modifier.nestedScroll(pullToRefreshState.nestedScrollConnection)) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            item {
                AnimatedVisibility(
                    visible = showHeader,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    Column(modifier = Modifier.padding(top = 50.dp)) {
                        Header(
                            title = stringResource(R.string.dictionary),
                            trashVisible = true,
                            navigateTo = navigateToBackStack,
                            trashOnClick = { onEvent(DictionaryEvent.OnClearAll) }
                        )
                        SearchContainer(
                            searchQuery = state.searchQuery,
                            onValueChanged = { query ->
                                onEvent(DictionaryEvent.OnSearchQueryChanged(query))
                            }
                        )
                        Spacer(Modifier.height(21.dp))
                    }
                }
            }

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
                Spacer(modifier = Modifier.height(7.dp))
            }
        }

        PullToRefreshContainer(
            modifier = Modifier.align(Alignment.TopCenter),
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