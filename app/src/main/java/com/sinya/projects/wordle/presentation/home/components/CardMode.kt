package com.sinya.projects.wordle.presentation.home.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyShapes
import com.sinya.projects.wordle.ui.theme.WordyTypography
import com.sinya.projects.wordle.ui.theme.gray800

@Composable
fun CardMode(
    @StringRes titleRes: Int,
    @DrawableRes imageRes: Int,
    modifier: Modifier,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = WordyShapes.large,
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 10.dp, horizontal = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(titleRes),
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                color = WordyColor.colors.textOnColorCard,
                style = WordyTypography.titleLarge
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 7.dp, bottom = 10.dp)
                        .clip(CircleShape)
                        .border(1.dp, gray800, CircleShape)
                        .background(color = Color.White)
                        .size(41.dp)
                        .scale(0.75f),
                    colorFilter = ColorFilter.tint(Color.Black)
                )
                Button(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(22.dp),
                    shape = WordyShapes.large,
                    colors = ButtonDefaults.buttonColors(containerColor = WordyColor.colors.backgroundActiveBtnMkII),
                    onClick = onClick,
                    contentPadding = PaddingValues(vertical = 0.dp)
                ) {
                    Text(
                        stringResource(R.string.play),
                        fontSize = 12.sp,
                        color = color,
                        style = WordyTypography.bodyMedium
                    )
                }
            }
        }
    }
}