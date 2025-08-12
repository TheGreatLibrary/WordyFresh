package com.sinya.projects.wordle.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.data.remote.supabase.entity.Profiles
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.screen.profile.components.AvatarPicker
import com.sinya.projects.wordle.ui.features.CardColumn
import com.sinya.projects.wordle.ui.features.CustomCard
import com.sinya.projects.wordle.ui.features.RoundedBackText
import com.sinya.projects.wordle.ui.features.RowLink
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography
import com.sinya.projects.wordle.ui.theme.white

@Composable
fun ProfileInAccountView(
    onPickClicked: () -> Unit,
    state: ProfileUiState.Success,
    navigateTo: (ScreenRoute) -> Unit,
) {
    val context = LocalContext.current

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        Spacer(Modifier.height(5.dp))
        AvatarPicker(
            imageUri = state.avatarUri,
            onPickClicked = onPickClicked
        )
        Text(
            text = state.profile.nickname,
            fontSize = 20.sp,
            color = WordyColor.colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(0.6f),
            textAlign = TextAlign.Center
        )
        RoundedBackText(text = state.email)
        Spacer(Modifier.height(2.dp))
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
        Spacer(Modifier.height(1.dp))
        CustomCard(modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .fillMaxWidth()
            .clickable { state.onEvent(ProfileUiEvent.SignOut(context)) }) {
            Row(
                modifier = Modifier.padding(vertical = 11.dp, horizontal = 13.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.prof_exit),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 9.dp)
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
}

@Preview(showBackground = true)
@Composable
private fun ProfileInAccountPreview() {
    ProfileInAccountView(
        onPickClicked = { },
        state = ProfileUiState.Success(
            profile = Profiles(
                nickname = "Sinya",
                avatarUrl = "",
                createdAt = ""
            ),
            email = "sz24@gmail.com",
            avatarUri = null,
            onEvent = { }
        ),
        navigateTo = { },
    )
}