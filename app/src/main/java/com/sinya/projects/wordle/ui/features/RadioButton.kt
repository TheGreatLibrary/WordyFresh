package com.sinya.projects.wordle.ui.features

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun RadioButton(
    modifier: Modifier,
    painter: Painter? = null,
    text: String? = null,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    shape: Shape = MaterialTheme.shapes.extraLarge,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(containerColor, shape)
            .clip(shape)
            .clickable { onClick() }
            .then(modifier),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        painter?.let {
            Icon(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = contentColor
            )
        }
        text?.let {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = contentColor
            )
        }
    }
}