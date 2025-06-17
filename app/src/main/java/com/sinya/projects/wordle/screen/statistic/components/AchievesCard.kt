package com.sinya.projects.wordle.screen.statistic.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.ui.features.CustomCard
import com.sinya.projects.wordle.ui.theme.WordleColor
import com.sinya.projects.wordle.ui.theme.WordleTypography
import com.sinya.projects.wordle.ui.theme.green600
import com.sinya.projects.wordle.ui.theme.green800
import com.sinya.projects.wordle.ui.theme.white

@Composable
fun AchievesCard(
    navigateTo: (ScreenRoute) -> Unit
) {
    CustomCard(
        modifier = Modifier
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { navigateTo(ScreenRoute.Achieves) }
                .padding(horizontal = 25.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(R.drawable.stat_achieve),
                modifier = Modifier
                    .clip(CircleShape)
                    .background(color = green600)
                    .border(2.dp, green800, CircleShape)
                    .size(59.dp)
                    .scale(0.85f),
                contentDescription = "achieve",
                colorFilter = ColorFilter.tint(white)
            )
            Text(
                stringResource(R.string.achievements),
                fontSize = 18.sp,
                color = WordleColor.colors.textCardPrimary,

                modifier = Modifier.fillMaxWidth(0.7f),
                style = WordleTypography.bodyLarge
            )
            Image(
                painter = painterResource(R.drawable.arrow),
                modifier = Modifier.size(20.dp),
                contentDescription = "Перейти",
                colorFilter = ColorFilter.tint(green800)
            )
        }
    }
}