package com.sinya.projects.wordle.ui.features

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.sinya.projects.wordle.ui.theme.WordyColor

@Composable
fun PlaceholderBox(
    modifier: Modifier,
    shape: CornerBasedShape,
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                color = WordyColor.colors.backgroundBoxDefault.copy(alpha = 0.3f),
                shape = shape
            )
    )
}