package com.sinya.projects.wordle.ui.features

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.screen.home.HomeUiEvent
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyShapes
import com.sinya.projects.wordle.ui.theme.WordyTypography
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
        RoundedButton(
            modifier = Modifier.fillMaxWidth(0.9f),
            colors = ButtonDefaults.buttonColors(containerColor = WordyColor.colors.backgroundActiveBtnMkII),
            contentPadding = PaddingValues(vertical = 3.dp, horizontal = 15.dp),
            onClick = { }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.social_google),
                    contentDescription = null,
                    modifier = Modifier.size(25.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Google",
                    fontSize = 17.sp,
                    color = WordyColor.colors.textForActiveBtnMkII,
                    style = WordyTypography.bodyMedium
                )
            }

        }
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
                .background(color = WordyColor.colors.textPrimary)
        )
        Text(
            text = title,
            color = WordyColor.colors.textPrimary
        )
        Spacer(
            Modifier
                .width(58.dp)
                .height(1.dp)
                .background(color = WordyColor.colors.textPrimary)
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
            modifierBox = Modifier
                .shadow(
                    elevation = 7.dp,
                    spotColor = WordyColor.colors.shadowColor,
                    shape = WordyShapes.extraLarge
                )
                .size(53.dp)
                .background(color = white, shape = WordyShapes.extraLarge),
            modifierImage = Modifier.fillMaxSize(0.6f)
        ) { navigateTo() }
        ImageButton(
            image = R.drawable.social_vk,
            modifierBox = Modifier
                .shadow(
                    elevation = 7.dp,
                    spotColor = WordyColor.colors.shadowColor,
                    shape = WordyShapes.extraLarge
                )
                .size(53.dp)
                .background(color = white, shape = WordyShapes.extraLarge),
            modifierImage = Modifier.fillMaxSize(0.6f)
        ) { navigateTo() }
        ImageButton(
            image = R.drawable.social_tg,
            modifierBox = Modifier
                .shadow(
                    elevation = 7.dp,
                    spotColor = WordyColor.colors.shadowColor,
                    shape = WordyShapes.extraLarge
                )
                .size(53.dp)
                .background(color = white, shape = WordyShapes.extraLarge),
            modifierImage = Modifier.fillMaxSize(0.6f)
        ) { navigateTo() }
    }
}

@Composable
fun RowVariableAuth(
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
            color = WordyColor.colors.textPrimary
        )
        Text(
            text = text,
            modifier = Modifier.clickable { navigateTo() },
            style = WordyTypography.labelSmall
        )
    }
}