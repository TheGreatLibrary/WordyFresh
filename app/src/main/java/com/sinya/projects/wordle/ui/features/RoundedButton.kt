package com.sinya.projects.wordle.ui.features

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyShapes

@Composable
fun RoundedButton(
    elevation: Int = 8,
    modifier: Modifier,
    colors: ButtonColors,
    onClick: () -> Unit,
    contentPadding: PaddingValues,
    body: @Composable RowScope.() -> Unit
) {
    Button(
        modifier = modifier.shadow(
            elevation = elevation.dp,
            spotColor = WordyColor.colors.shadowColor,
            shape = WordyShapes.extraLarge
        ),
        shape = WordyShapes.extraLarge,
        colors = colors,
        onClick = onClick,
        contentPadding = contentPadding,
        content = body
    )
}