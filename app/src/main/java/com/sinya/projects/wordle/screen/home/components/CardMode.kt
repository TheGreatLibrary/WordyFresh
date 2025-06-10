package com.sinya.projects.wordle.screen.home.components

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.ui.theme.WordleColor
import com.sinya.projects.wordle.ui.theme.WordleShapes
import com.sinya.projects.wordle.ui.theme.WordleTypography
import com.sinya.projects.wordle.ui.theme.gray800
import com.sinya.projects.wordle.ui.theme.white


@Composable
fun CardMode(
    image: Int,
    color: Color,
    modifier: Modifier,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .shadow(10.dp, spotColor = gray800, shape = WordleShapes.large)
            .then(modifier),
        shape = WordleShapes.large,
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 10.dp, horizontal = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                title,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                color = WordleColor.colors.textOnColorCard,
                style = WordleTypography.titleLarge
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(image),
                    contentDescription = "iconCont",
                    modifier = Modifier
                        .padding(top = 7.dp, bottom = 10.dp)
                        .clip(CircleShape)
                        .border(1.dp, gray800, CircleShape)
                        .background(color = white)
                        .size(41.dp)
                        .scale(0.75f),
                    colorFilter = ColorFilter.tint(Color.Black)
                )
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(22.dp)
                        .padding(horizontal = 17.dp),
                    shape = WordleShapes.large,
                    colors = ButtonDefaults.buttonColors(containerColor = WordleColor.colors.backgroundActiveBtnMkII),
                    onClick = onClick,
                    contentPadding = PaddingValues(vertical = 0.dp)
                ) {
                    Text(
                        stringResource(R.string.play),
                        fontSize = 12.sp,
                        color = WordleColor.colors.textForActiveBtnMkII,
                        style = WordleTypography.bodyMedium
                    )
                }
            }
        }
    }
}