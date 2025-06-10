package com.sinya.projects.wordle.ui.features

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.ui.theme.WordleColor
import com.sinya.projects.wordle.ui.theme.WordleShapes
import com.sinya.projects.wordle.ui.theme.WordleTypography
import com.sinya.projects.wordle.ui.theme.white

@Composable
fun AuthFooter(
    title: String,
    titleFooter: String,
    textLink: String,
    navigateTo: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RowVariableTitle(
            title = title
        )
        RowSocialAuth(
            navigateTo = navigateTo
        )
        RowVariableAuth(
            title = titleFooter,
            text = textLink,
            navigateTo = navigateTo
        )
    }
}

@Composable
private fun RowVariableTitle(
    title: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            space = 5.dp,
            alignment = Alignment.CenterHorizontally
        ),
    ) {
        Spacer(
            Modifier
                .width(58.dp)
                .height(1.dp)
                .background(color = WordleColor.colors.textPrimary)
        )
        Text(
            text = title,
            color = WordleColor.colors.textPrimary
        )
        Spacer(
            Modifier
                .width(58.dp)
                .height(1.dp)
                .background(color = WordleColor.colors.textPrimary)
        )
    }
}

@Composable
private fun RowSocialAuth(
    navigateTo: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            space = 13.dp,
            alignment = Alignment.CenterHorizontally
        ),
    ) {
        ImageButton(
            image = R.drawable.social_google,
            modifierButton = Modifier
                .shadow(
                    elevation = 7.dp,
                    spotColor = WordleColor.colors.shadowColor,
                    shape = WordleShapes.extraLarge
                )
                .size(53.dp)
                .background(color = white, shape = CircleShape),
            modifierIcon = Modifier.fillMaxSize(0.6f)
        ) { navigateTo() }
        ImageButton(
            image = R.drawable.social_vk,
            modifierButton = Modifier
                .shadow(
                    elevation = 7.dp,
                    spotColor = WordleColor.colors.shadowColor,
                    shape = WordleShapes.extraLarge
                )
                .size(53.dp)
                .background(color = white, shape = CircleShape),
            modifierIcon = Modifier.fillMaxSize(0.6f)
        ) { navigateTo() }
        ImageButton(
            image = R.drawable.social_tg,
            modifierButton = Modifier
                .shadow(
                    elevation = 7.dp,
                    spotColor = WordleColor.colors.shadowColor,
                    shape = WordleShapes.extraLarge
                )
                .size(53.dp)
                .background(color = white, shape = CircleShape),
            modifierIcon = Modifier.fillMaxSize(0.6f)
        ) { navigateTo() }
    }
}

@Composable
private fun RowVariableAuth(
    title: String,
    text: String,
    navigateTo: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            space = 5.dp,
            alignment = Alignment.CenterHorizontally
        ),
    ) {
        Text(
            text = title,
            color = WordleColor.colors.textPrimary
        )
        Text(
            text = text,
            modifier = Modifier.clickable { navigateTo() },
            style = WordleTypography.labelSmall
        )
    }
}