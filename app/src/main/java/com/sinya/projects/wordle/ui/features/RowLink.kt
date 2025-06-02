package com.sinya.projects.wordle.ui.features

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.ui.theme.WordleColor
import com.sinya.projects.wordle.ui.theme.WordleTypography

@Composable
fun RowLink(
    title: String,
    mode: String,
    @DrawableRes icon: Int,
    @DrawableRes icon2: Int,
    navigateTo: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { navigateTo() }
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RowImageWithText(
            modifier = Modifier.weight(0.7f),
            icon = icon,
            title = title
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                mode,
                fontSize = 13.sp,
                color = WordleColor.colors.textCardSecondary,
                modifier = Modifier.padding(end = 4.dp),
                style = WordleTypography.bodyMedium
            )
            Image(
                painter = painterResource(icon2),
                contentDescription = null,
                Modifier.size(15.dp),
            colorFilter = ColorFilter.tint(color = WordleColor.colors.textCardSecondary)
            )
        }
    }
}