package com.sinya.projects.wordle.ui.features

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.ui.theme.WordleColor
import com.sinya.projects.wordle.ui.theme.WordleTypography

@Composable
fun RowImageWithText(
    modifier: Modifier,
    icon: Int,
    title: String
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            Modifier
                .padding(end = 9.dp)
                .size(24.dp)
                .clip(CircleShape)
                .background(color = WordleColor.colors.backgroundIcon)
                .scale(0.75f),
            colorFilter = ColorFilter.tint(WordleColor.colors.foregroundIcon)
        )
        Text(
            title,
            fontSize = 15.sp,
            color = WordleColor.colors.textCardPrimary,
            style = WordleTypography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}