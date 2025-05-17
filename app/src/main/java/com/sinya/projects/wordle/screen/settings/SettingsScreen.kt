package com.sinya.projects.wordle.screen.settings

import android.app.Activity
import android.os.Build
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.data.local.datastore.LocaleViewModel
import com.sinya.projects.wordle.data.local.datastore.ThemeViewModel
import com.sinya.projects.wordle.navigation.Header
import com.sinya.projects.wordle.ui.theme.WordleTypography
import com.sinya.projects.wordle.ui.theme.gray200
import com.sinya.projects.wordle.ui.theme.gray300
import com.sinya.projects.wordle.ui.theme.gray400
import com.sinya.projects.wordle.ui.theme.gray600
import com.sinya.projects.wordle.ui.theme.green600
import com.sinya.projects.wordle.ui.theme.green800
import com.sinya.projects.wordle.ui.theme.white

@Composable
fun SettingsScreen(themeViewModel: ThemeViewModel, localeViewModel: LocaleViewModel, navController: NavController) {
    val context = LocalContext.current
    val isDark by themeViewModel.isDarkMode.collectAsState()
    val lang by localeViewModel.language.collectAsState()

    LaunchedEffect(Unit) {
        themeViewModel.themeChanged.collect {
            // После подтверждённой смены языка — перезапускаем
            (context as? Activity)?.recreate()
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 7.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        Header(stringResource(R.string.settings_screen), false, navController)
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
                RowSettingLink("Язык", "Русский", R.drawable.icon_home) { navController.navigate("language") }
                RowSettingLink("Темный режим", "Светлый", R.drawable.icon_home) { navController.navigate("themeMode") }
                RowSettingLink("Как играть", "Пройти", R.drawable.icon_home) {}
            }
        }
        Card(
            Modifier
                .fillMaxWidth()
                .background(color = white, RoundedCornerShape(12.dp))
                .shadow(elevation = 5.dp, spotColor = green800, shape = RoundedCornerShape(8.dp))
        ) {
            Column(
                modifier = Modifier
                    .background(color = white)
                    .padding(vertical = 16.dp, horizontal = 13.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                RowSettingSwitch("Музыка",  R.drawable.icon_home)
                RowSettingSwitch("Звуковые эффекты",  R.drawable.icon_home)
                RowSettingSwitch("Вибрация",  R.drawable.icon_home)
              
            }
        }
        Card(
            Modifier
                .fillMaxWidth()
                .background(color = white, RoundedCornerShape(12.dp))
                .shadow(elevation = 5.dp, spotColor = green800, shape = RoundedCornerShape(8.dp))
        ) {
            Column(
                modifier = Modifier
                    .background(color = white)
                    .padding(vertical = 16.dp, horizontal = 13.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                RowSettingSwitch("Конфетти-анимация",  R.drawable.icon_home)
                RowSettingSwitch("Взрослые слова",  R.drawable.icon_home)
                RowSettingLink("Расположение DELETE", "Слева", R.drawable.icon_home) {}
            }
        }
        Card(
            Modifier
                .fillMaxWidth()
                .background(color = white, RoundedCornerShape(12.dp))
                .shadow(elevation = 5.dp, spotColor = green800, shape = RoundedCornerShape(8.dp))
        ) {
            Row(
                Modifier
                    .background(color = white)
                    .fillMaxWidth()
                    .padding(horizontal = 13.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_search),
                        contentDescription = "d",
                        Modifier
                            .padding(end = 9.dp)
                            .size(23.dp)
                            .clip(CircleShape)
                            .background(color = gray200)
                            .scale(0.8f)
                    )
                    Text("Написать в техподдержку", fontSize = 15.sp, color = gray600, style = WordleTypography.bodyMedium)
                }
                Image(
                    painter = painterResource(R.drawable.diagonal_arrow),
                    contentDescription = "d",
                    Modifier.size(15.dp),
                )
            }
        }
        AppVersionInfo()
    }
}

@Composable
fun RowSettingLink(title: String, mode: String, @DrawableRes icon: Int, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = "d",
                Modifier
                    .padding(end = 9.dp)
                    .size(23.dp)
                    .clip(CircleShape)
                    .background(color = gray200)
                    .scale(0.8f),
                colorFilter = ColorFilter.tint(gray400)
            )
            Text(title, fontSize = 15.sp, color = gray600, style = WordleTypography.bodyMedium)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(mode, fontSize = 13.sp, color = gray300, modifier = Modifier.padding(end = 4.dp), style = WordleTypography.bodyMedium)
            Image(
                painter = painterResource(R.drawable.arrow),
                contentDescription = "d",
                Modifier.size(15.dp),
                colorFilter = ColorFilter.tint(color = gray200)
            )
        }
    }
}

@Composable
fun RowSettingSwitch(title: String, @DrawableRes icon: Int) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = "d",
                Modifier
                    .padding(end = 9.dp)
                    .size(23.dp)
                    .clip(CircleShape)
                    .background(color = gray200)
                    .scale(0.8f),
                colorFilter = ColorFilter.tint(gray400)
            )
            Text(title, fontSize = 15.sp, color = gray600, style = WordleTypography.bodyMedium)
        }
        var isChecked by remember { mutableStateOf(false) } // Храним состояние
        Switch(
            checked = isChecked,
            onCheckedChange = {
                isChecked = it
            },
            Modifier
                .size(28.dp, 16.dp)
                .scale(0.74f),
            colors = SwitchDefaults.colors(
                checkedThumbColor = green800,
                checkedTrackColor = green600, // Цвет трека в включенном состоянии
                uncheckedThumbColor = gray400, // Цвет кружка в выключенном состоянии
                uncheckedTrackColor = gray200, // Цвет трека в выключенном состоянии
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}

@Composable
fun AppVersionInfo() {
    val context = LocalContext.current
    val packageInfo = remember {
        context.packageManager.getPackageInfo(context.packageName, 0)
    }
    val versionName = packageInfo.versionName
    val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        packageInfo.longVersionCode.toInt()
    } else {
        @Suppress("DEPRECATION")
        packageInfo.versionCode
    }
    Text("Версия: $versionName ($versionCode)", fontSize = 14.sp, color = Color.White, style = WordleTypography.bodyMedium)
}

@Composable
fun DropdownMenuLanguage(selectedLang: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val languages = listOf("ru", "en")

    Box {
        Text(
            text = selectedLang,
            modifier = Modifier
                .clickable { expanded = true }
                .padding(8.dp)
        )

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            languages.forEach { lang ->
                DropdownMenuItem(
                    onClick = {
                        onSelect(lang)
                        expanded = false
                    },
                    text = { Text(if (lang == "ru") "Русский" else "English") }
                )
            }
        }
    }
}