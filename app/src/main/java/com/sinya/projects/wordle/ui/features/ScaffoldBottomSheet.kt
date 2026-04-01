package com.sinya.projects.wordle.ui.features

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldBottomSheet(
    scaffoldState: BottomSheetScaffoldState,
    sheetContent: @Composable (ColumnScope.() -> Unit),
    content: @Composable (PaddingValues) -> Unit
) {
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 56.dp,
        sheetShadowElevation = 8.dp,
        sheetTonalElevation = 0.dp,
        sheetShape = MaterialTheme.shapes.large,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        sheetContainerColor = MaterialTheme.colorScheme.surface,
        sheetDragHandle = { BottomSheetDefaults.DragHandle() },
        sheetContent = sheetContent
    ) { padding ->
        content(padding)
    }
}