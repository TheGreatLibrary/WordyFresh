package com.sinya.projects.wordle.presentation.dictionary.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.presentation.dictionary.DictionaryEvent
import com.sinya.projects.wordle.presentation.dictionary.DictionaryUiState
import com.sinya.projects.wordle.ui.features.Header
import com.sinya.projects.wordle.ui.features.SortBlock
import com.sinya.projects.wordle.ui.theme.WordyColor

@Composable
fun BoxScope.DictionaryHeader(
    modifier: Modifier,
    state: DictionaryUiState.Success,
    onEvent: (DictionaryEvent) -> Unit,
    navigateToBackStack: () -> Unit,
) {
    val context = LocalContext.current
    val modesFlattened = remember(state.dictionaryList, state.modes) {
        state.modes.flatMap { mode ->
            mode.categories<Any, Any>(state.dictionaryList, context).map { filter ->
                filter to mode
            }
        }
    }

    Column(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .then(modifier)
            .background(WordyColor.colors.background, RoundedCornerShape(bottomEnd = 12.dp, bottomStart = 12.dp))
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp)
    ) {
        Header(
            title = stringResource(R.string.dictionary),
            trashVisible = true,
            navigateTo = navigateToBackStack,
            trashOnClick = { onEvent(DictionaryEvent.OnClearAll) }
        )
        modesFlattened.forEach { (filter, mode) ->
            SortBlock(
                title = stringResource(filter.titleRes),
                selectedOption = filter.selectedValue,
                radioOptions = filter.options,
                onOptionSelected = { value ->
                    onEvent(
                        DictionaryEvent.SortParamChange(
                            mode,
                            filter.onSelect(value)
                        )
                    )
                }
            )
        }
        SearchContainer(
            searchQuery = state.searchQuery,
            onValueChanged = { query ->
                onEvent(DictionaryEvent.OnSearchQueryChanged(query))
            },
            onVibrate = { type ->
                onEvent(DictionaryEvent.OnVibrate(type))
            }
        )
    }
}