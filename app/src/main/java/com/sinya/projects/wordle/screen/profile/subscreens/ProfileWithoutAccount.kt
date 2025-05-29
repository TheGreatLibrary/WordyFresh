package com.sinya.projects.wordle.screen.profile.subscreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.ui.components.RoundedButton
import com.sinya.projects.wordle.ui.theme.WordleColor
import com.sinya.projects.wordle.ui.theme.WordleTypography
import com.sinya.projects.wordle.ui.theme.gray800

@Composable
fun ProfileWithoutAccount(navController: NavController) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Spacer(Modifier.fillMaxWidth())
        Column(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.sync_to_save_stat),
                color = Color.White,
                style = WordleTypography.titleLarge,
                fontSize = 21.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(15.dp))
            Text(
                text = stringResource(R.string.play_with_friends),
                color = WordleColor.colors.background,
                fontSize = 16.sp,
                style = WordleTypography.titleLarge,
                textAlign = TextAlign.Center
            )
        }
        Column(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 130.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RoundedButton(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .shadow(8.dp, spotColor = gray800),
                ButtonDefaults.buttonColors(containerColor = WordleColor.colors.backgroundBtnMkIII),
                {
                    navController.navigate("register")
                }
            ) {
                Text(
                    stringResource(R.string.sign_up),
                    fontSize = 18.sp,
                    color = WordleColor.colors.textColorMkII,
                    style = WordleTypography.bodyMedium
                )
            }
            Spacer(Modifier.height(19.dp))
            RoundedButton(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .shadow(8.dp, spotColor = gray800),
                ButtonDefaults.buttonColors(containerColor = WordleColor.colors.backgroundBtnMkI),
                {
                    navController.navigate("login")
                }
            ) {
                Text(
                    stringResource(R.string.login),
                    fontSize = 18.sp,
                    color = WordleColor.colors.onTextColor,
                    style = WordleTypography.bodyMedium
                )
            }
        }
    }
}