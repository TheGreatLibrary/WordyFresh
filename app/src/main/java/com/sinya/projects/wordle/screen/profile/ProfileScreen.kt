package com.sinya.projects.wordle.screen.profile

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.data.local.database.AppDatabase
import com.sinya.projects.wordle.domain.model.entity.Profiles
import com.sinya.projects.wordle.navigation.Header
import com.sinya.projects.wordle.screen.settings.RowSettingLink
import com.sinya.projects.wordle.ui.components.RoundedBackgroundText
import com.sinya.projects.wordle.ui.components.RoundedButton
import com.sinya.projects.wordle.ui.theme.WordleColor
import com.sinya.projects.wordle.ui.theme.WordleTypography
import com.sinya.projects.wordle.ui.theme.gray200
import com.sinya.projects.wordle.ui.theme.gray400
import com.sinya.projects.wordle.ui.theme.gray800
import com.sinya.projects.wordle.ui.theme.green800
import com.sinya.projects.wordle.ui.theme.white
import io.github.jan.supabase.SupabaseClient

@Composable
fun ProfileScreen(
    navController: NavController,
    supabase: SupabaseClient
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }

    val viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModel.provideFactory(db, supabase)
    )
    val profile = viewModel.profile

    Column(
        Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header(stringResource(R.string.profile_screen), false, navController)

        when {
            profile != null -> WithAccount(viewModel)
            viewModel.error != null -> {
              //  Text("Ошибка: ${viewModel.error}")
                WithoutAccount(navController)
            }
            else -> CircularProgressIndicator()
        }
    }
}

@Composable
fun WithoutAccount(navController: NavController) {
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
                text = "Синхронизируйся, чтобы сохранять статистику.",
                color = Color.White,
                style = WordleTypography.titleLarge,
                fontSize = 21.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(15.dp))
            Text(
                text = "Играй с друзьями и загадывай им слова прямо из приложения!",
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

@Composable
fun WithAccount(viewModel: ProfileViewModel) {
    val profile = viewModel.profile as Profiles
    val context = LocalContext.current

    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            viewModel.updateAvatar(context, it)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadAvatar(context)
    }

    Spacer(Modifier.height(5.dp))
    AvatarPicker(
        imageUri = viewModel.avatarUri,
        onPickClicked = { pickImageLauncher.launch("image/*") }
    )
    Spacer(Modifier.height(10.dp))
    Text(profile.nickname, fontSize = 20.sp)
    Spacer(Modifier.height(8.dp))
    RoundedBackgroundText(viewModel.getEmail())
    Spacer(Modifier.height(10.dp))
    Card(
        Modifier
            .fillMaxWidth()
            .padding(top = 18.dp)
            .background(color = white, RoundedCornerShape(12.dp))
            .shadow(elevation = 5.dp, spotColor = green800, shape = RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier
                .background(color = white)
                .padding(vertical = 16.dp, horizontal = 13.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RowSettingLink("Редактировать профиль", "", R.drawable.enter)
            RowSettingLink("Друзья", "", R.drawable.ic_serch_dict)
            RowSettingLink("Уведомления", "", R.drawable.ic_trash)
        }
    }
    Card(
        Modifier
            .fillMaxWidth()
            .padding(top = 18.dp)
            .background(color = white, RoundedCornerShape(12.dp))
            .shadow(elevation = 5.dp, spotColor = green800, shape = RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier
                .background(color = white)
                .padding(vertical = 16.dp, horizontal = 13.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RowSettingLink("О приложении", "", R.drawable.enter)
            RowSettingLink("Политика конфиденциальности", "", R.drawable.ic_serch_dict)
            RowSettingLink("Условия использования", "", R.drawable.ic_trash)
        }
    }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 18.dp)
            .clickable { viewModel.signOut(context) }
            .background(color = white, RoundedCornerShape(99.dp))
            .padding(vertical = 11.dp, horizontal = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            Icons.Default.ExitToApp,
            contentDescription = "d",
            Modifier
                .padding(end = 9.dp)
                .size(23.dp)
                .clip(CircleShape)
                .background(color = gray200)
                .scale(0.8f),
            colorFilter = ColorFilter.tint(gray400)
        )
        Text("Выйти из аккаунта", fontSize = 15.sp, color = WordleColor.colors.secondary, style = WordleTypography.bodyMedium)
    }
}

@Composable
fun AvatarPicker(
    imageUri: Uri?,
    onPickClicked: () -> Unit
) {
    val refreshedUri = imageUri?.buildUpon()?.appendQueryParameter("ts", System.currentTimeMillis().toString())?.build()


    Box {
        Box(
            modifier = Modifier
                .size(111.dp)
                .clip(CircleShape)
                .border(2.dp, WordleColor.colors.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                if (imageUri != null) rememberAsyncImagePainter(refreshedUri) else painterResource(R.drawable.avatar),
                contentDescription = null,
                modifier = Modifier
                    .size(106.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        IconButton(
            onClick = {  onPickClicked()  },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 10.dp)
                .background(WordleColor.colors.primary, CircleShape)
                .size(26.dp)
        ) {
            Icon(painterResource(R.drawable.ic_camera), contentDescription = "Edit")
        }
    }
}

