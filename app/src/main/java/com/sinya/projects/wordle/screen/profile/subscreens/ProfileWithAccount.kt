package com.sinya.projects.wordle.screen.profile.subscreens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.domain.model.entity.Profiles
import com.sinya.projects.wordle.screen.profile.ProfileViewModel
import com.sinya.projects.wordle.screen.settings.CardColumn
import com.sinya.projects.wordle.screen.settings.RowSettingLink
import com.sinya.projects.wordle.ui.components.RoundedBackgroundText
import com.sinya.projects.wordle.ui.theme.WordleColor
import com.sinya.projects.wordle.ui.theme.WordleTypography
import com.sinya.projects.wordle.ui.theme.red
import com.sinya.projects.wordle.ui.theme.white

@Composable
fun ProfileWithAccount(
    viewModel: ProfileViewModel,
    profile: Profiles,
    avatarUri: Uri?,
    navController: NavController
) {
    val pickImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                viewModel.updateAvatar(it)
            }
        }

    LaunchedEffect(Unit) {
        viewModel.loadAvatar()
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        Spacer(Modifier.height(5.dp))
        AvatarPicker(
            imageUri = avatarUri,
            onPickClicked = { pickImageLauncher.launch("image/*") }
        )
        Text(profile.nickname, fontSize = 20.sp)
        RoundedBackgroundText(viewModel.getEmail())
        Spacer(Modifier.height(2.dp))
        CardColumn {
            RowSettingLink(
                stringResource(R.string.rewrite_screen),
                "",
                R.drawable.prof_rewrite,
                R.drawable.arrow
            ) {}
            RowSettingLink(
                stringResource(R.string.friends_screen),
                "",
                R.drawable.prof_friends,
                R.drawable.arrow
            ) {}
            RowSettingLink(
                stringResource(R.string.notification_screen),
                "",
                R.drawable.prof_notify,
                R.drawable.arrow
            ) {}
        }
        CardColumn {
            RowSettingLink(
                stringResource(R.string.about_app_screen),
                "",
                R.drawable.prof_about,
                R.drawable.arrow
            ) {}
            RowSettingLink(
                stringResource(R.string.policy_privacy),
                "",
                R.drawable.prof_privacy,
                R.drawable.arrow
            ) {}
            RowSettingLink(
                stringResource(R.string.terms_of_use),
                "",
                R.drawable.prof_terms,
                R.drawable.arrow
            ) {}
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .clip(RoundedCornerShape(99.dp))
                .background(color = white)
                .clickable { viewModel.signOut() }
                .padding(vertical = 11.dp, horizontal = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painterResource(R.drawable.prof_exit),
                contentDescription = null,
                Modifier
                    .padding(end = 9.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(color = red)
                    .scale(0.75f),
                colorFilter = ColorFilter.tint(white)
            )
            Text(
                stringResource(R.string.exit),
                fontSize = 15.sp,
                color = red,
                style = WordleTypography.bodyMedium
            )
        }
    }
}

@Composable
fun AvatarPicker(
    imageUri: Uri?,
    onPickClicked: () -> Unit
) {
    Box {
        Avatar(
            modifier = Modifier
                .size(111.dp)
                .clip(CircleShape)
                .border(2.dp, WordleColor.colors.primary, CircleShape),
            imageUri
        )
        IconButton(
            onClick = { onPickClicked() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 10.dp)
                .background(WordleColor.colors.primary, CircleShape)
                .size(26.dp)
        ) {
            Icon(painterResource(R.drawable.prof_camera), contentDescription = "Edit")
        }
    }
}

@Composable
fun Avatar(modifier: Modifier, imageUri: Uri?, onClicked: (() -> Unit)? = null) {
    val refreshedUri =
        imageUri?.buildUpon()?.appendQueryParameter("ts", System.currentTimeMillis().toString())
            ?.build()

    Box(
        modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            if (imageUri != null) rememberAsyncImagePainter(refreshedUri) else painterResource(R.drawable.avatar),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(0.93f)
                .clip(CircleShape)
                .clickable { onClicked?.let { it() } },
            contentScale = ContentScale.Crop
        )
    }
}


@Composable
fun ProfilePlaceholder() {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .size(111.dp)
                .clip(CircleShape)
                .background(Color.Gray.copy(alpha = 0.3f))
        )
        Spacer(Modifier.height(12.dp))
        Box(
            Modifier
                .height(20.dp)
                .width(100.dp)
                .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
        )
        Spacer(Modifier.height(8.dp))
        repeat(3) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(vertical = 4.dp)
                    .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            )
        }
    }
}