package com.sinya.projects.wordle.presentation.profile.subscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.ui.features.RoundedButton
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun ProfileOutAccount(
    navigateTo: (ScreenRoute) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(Modifier)

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(9.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.sync_to_save_stat),
                color = WordyColor.colors.textPrimary,
                style = WordyTypography.titleLarge,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.play_with_friends),
                color = WordyColor.colors.textCardSecondary,
                style = WordyTypography.bodyMedium,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier)

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            RoundedButton(
                modifier = Modifier.fillMaxWidth(0.7f),
                colors = ButtonDefaults.buttonColors(containerColor = WordyColor.colors.backgroundActiveBtnMkI),
                contentPadding = PaddingValues(vertical = 0.dp, horizontal = 10.dp),
                onClick = { navigateTo(ScreenRoute.Register) }
            ) {
                Text(
                    stringResource(R.string.sign_up),
                    fontSize = 16.sp,
                    color = WordyColor.colors.textForActiveBtnMkI,
                    style = WordyTypography.bodyMedium
                )
            }
            RoundedButton(
                modifier = Modifier.fillMaxWidth(0.7f),
                colors = ButtonDefaults.buttonColors(containerColor = WordyColor.colors.backgroundActiveBtnMkII),
                contentPadding = PaddingValues(vertical = 0.dp, horizontal = 10.dp),
                onClick = { navigateTo(ScreenRoute.Login) }
            ) {
                Text(
                    stringResource(R.string.sign_in),
                    fontSize = 16.sp,
                    color = WordyColor.colors.textForActiveBtnMkII,
                    style = WordyTypography.bodyMedium
                )
            }
        }

        Spacer(Modifier)
    }
}