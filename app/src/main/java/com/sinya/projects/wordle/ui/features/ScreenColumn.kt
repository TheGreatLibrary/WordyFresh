package com.sinya.projects.wordle.ui.features

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ScreenColumn(
    modifier: Modifier = Modifier,
    title: String? = null,
    navigateBack: (() -> Unit)? = null,
    trashVisible: Boolean = false,
    onTrashClick: (() -> Unit)? = null,
    spaced: Int = 9,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = modifier
                .widthIn(max = 550.dp)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spaced.dp)
        ) {
            if (title != null || navigateBack != null) {
                Header(
                    title = title.orEmpty(),
                    trashVisible = trashVisible,
                    trashOnClick = onTrashClick ?: {},
                    navigateTo = navigateBack ?: {}
                )
                Spacer(Modifier)
            }
            content()
        }
    }
}