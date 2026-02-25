package com.sinya.projects.wordle.presentation.settings.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.ui.features.PlaceholderBox
import com.sinya.projects.wordle.ui.features.ScreenColumn
import com.sinya.projects.wordle.ui.theme.WordyShapes

@Composable
fun SettingsPlaceholder(navigateToBackStack: () -> Unit, title: String) {
    ScreenColumn(
        title = title,
        navigateBack = navigateToBackStack
    ) {
        repeat(3) {
            PlaceholderBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = WordyShapes.small
            )
        }

        PlaceholderBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = WordyShapes.small
        )

        PlaceholderBox(
            modifier = Modifier
                .width(100.dp)
                .height(30.dp),
            shape = WordyShapes.medium
        )
    }
}