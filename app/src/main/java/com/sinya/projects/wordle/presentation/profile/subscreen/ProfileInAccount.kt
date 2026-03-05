package com.sinya.projects.wordle.presentation.profile.subscreen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.presentation.profile.ProfileEvent
import com.sinya.projects.wordle.presentation.profile.ProfileUiState
import com.sinya.projects.wordle.ui.features.AvatarPicker
import com.sinya.projects.wordle.ui.features.CardColumn
import com.sinya.projects.wordle.ui.features.CustomCard
import com.sinya.projects.wordle.ui.features.RoundedBackText
import com.sinya.projects.wordle.ui.features.RowLink
import com.sinya.projects.wordle.ui.features.ScreenColumn
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyShapes
import com.sinya.projects.wordle.ui.theme.WordyTypography
import com.sinya.projects.wordle.ui.theme.white

@Composable
fun ProfileInAccount(
    state: ProfileUiState.InAccount,
    onEvent: (ProfileEvent) -> Unit,
    navigateTo: (ScreenRoute) -> Unit,
    navigateBack: () -> Unit,
    title: String,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { onEvent(ProfileEvent.UpdateAvatar(it)) }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            onEvent(ProfileEvent.ErrorShown)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ScreenColumn(
            title = title,
            navigateBack = navigateBack
        ) {
            AvatarPicker(
                imageUri = state.avatarUri,
                isUploading = state.isUploadingAvatar,
                onPickClicked = { pickImageLauncher.launch("image/*") }
            )

            Text(
                text = state.profile.nickname,
                fontSize = 20.sp,
                color = WordyColor.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(0.7f),
                textAlign = TextAlign.Center
            )

            if (state.email.isNotEmpty()) {
                RoundedBackText(text = state.email)
            }

            Spacer(Modifier)

            CardColumn {
                RowLink(
                    title = stringResource(R.string.email_recovery),
                    mode = "",
                    icon = R.drawable.prof_email,
                    icon2 = R.drawable.arrow,
                    navigateTo = { navigateTo(ScreenRoute.ResetEmail) }
                )
                RowLink(
                    title = stringResource(R.string.rewrite_screen),
                    mode = "",
                    icon = R.drawable.prof_edit,
                    icon2 = R.drawable.arrow,
                    navigateTo = { navigateTo(ScreenRoute.Edit) }
                )
                RowLink(
                    title = stringResource(R.string.password_recovery),
                    mode = "",
                    icon = R.drawable.prof_password,
                    icon2 = R.drawable.arrow,
                    navigateTo = { navigateTo(ScreenRoute.ResetPassword) }
                )
                RowLink(
                    title = stringResource(R.string.about_app_screen),
                    mode = "",
                    icon = R.drawable.prof_about,
                    icon2 = R.drawable.arrow,
                    navigateTo = { navigateTo(ScreenRoute.About) }
                )

//            RowLink(
//                title = stringResource(R.string.friends_screen),
//                mode = "",
//                icon = R.drawable.prof_friends,
//                icon2 = R.drawable.arrow,
//                navigateTo = { navigateTo(ScreenRoute.SettingWithBar) }
//            )
//            RowLink(
//                title = stringResource(R.string.notification_screen),
//                mode = "",
//                icon = R.drawable.prof_notify,
//                icon2 = R.drawable.arrow,
//                navigateTo = { navigateTo(ScreenRoute.SettingWithBar) }
//            )
            }

            CustomCard(
                modifier = Modifier
                    .clip(WordyShapes.extraLarge)
                    .fillMaxWidth()
                    .clickable { onEvent(ProfileEvent.SignOut) }
            ) {
                Row(
                    modifier = Modifier
                        .padding(vertical = 11.dp, horizontal = 13.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.prof_exit),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(color = WordyColor.colors.secondary)
                            .scale(0.75f),
                        colorFilter = ColorFilter.tint(white)
                    )
                    Text(
                        text = stringResource(R.string.exit),
                        fontSize = 15.sp,
                        color = WordyColor.colors.secondary,
                        style = WordyTypography.bodyMedium
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}