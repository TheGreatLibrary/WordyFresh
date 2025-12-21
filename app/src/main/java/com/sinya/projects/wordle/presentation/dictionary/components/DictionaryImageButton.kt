package com.sinya.projects.wordle.presentation.dictionary.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.ui.theme.WordyColor

@Composable
fun DictionaryImageButton(
    @DrawableRes image: Int,
    onClick: () -> Unit
) {
    Image(
        painter = painterResource(image),
        contentDescription = null,
        modifier = Modifier
            .clip(RoundedCornerShape(5.dp))
            .background(WordyColor.colors.backgroundIcon)
            .size(28.dp)
            .clickable { onClick() }
            .scale(0.8f)
            .padding(2.dp),
        colorFilter = ColorFilter.tint(WordyColor.colors.foregroundIcon)
    )
}