package com.sinya.projects.wordle.ui.features

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ScreenColumn(
    modifier: Modifier = Modifier,
    title: String? = null,
    navigateBack: (() -> Unit)? = null,
    trashVisible: Boolean = false,
    onTrashClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(9.dp)
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