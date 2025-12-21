package com.sinya.projects.wordle.presentation.settings.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.domain.enums.BackgroundSettings
import com.sinya.projects.wordle.domain.enums.TypeBackground
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyShapes

@Composable
fun BackgroundCardBox(
    item: BackgroundSettings,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(95.dp)
            .clip(WordyShapes.small)
            .background(brush = item.brushData.toBrush())
            .border(
                width = if (isActive) 2.dp else 0.dp,
                color = if (isActive) WordyColor.colors.primary else Color.Transparent,
                shape = WordyShapes.small
            )
            .clickable { onClick() }
    ) {
        when (item.type) {
            TypeBackground.SYSTEM -> {
                Image(
                    painter = painterResource(id = item.res!!),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            TypeBackground.DEFAULT -> {
                Icon(
                    painter = painterResource(id = R.drawable.stat_trash),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(32.dp),
                    tint = WordyColor.colors.textPrimary
                )
            }

            else -> Unit
        }
    }
}