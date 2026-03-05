package com.sinya.projects.wordle.ui.features

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.ui.theme.WordyColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomModalSheet(
    onDismissRequest: () -> Unit,
    sheetContent: @Composable (ColumnScope.() -> Unit)
) {
    ModalBottomSheet(
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        tonalElevation = 0.dp,
        onDismissRequest = {
            onDismissRequest()
        },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = WordyColor.colors.background
    ) {
        sheetContent()
    }
}