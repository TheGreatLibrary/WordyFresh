package com.sinya.projects.wordle.screen.settings

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material3.Card
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.data.local.datastore.AppDataStore
import com.sinya.projects.wordle.data.local.datastore.LocaleViewModel
import com.sinya.projects.wordle.data.local.datastore.ThemeViewModel
import com.sinya.projects.wordle.navigation.Header
import com.sinya.projects.wordle.screen.home.sendSupportEmail
import com.sinya.projects.wordle.ui.components.AppKeyboards
import com.sinya.projects.wordle.ui.components.AppLanguages
import com.sinya.projects.wordle.ui.components.AppThemes
import com.sinya.projects.wordle.ui.theme.WordleColor
import com.sinya.projects.wordle.ui.theme.WordleTypography
import com.sinya.projects.wordle.ui.theme.gray100
import com.sinya.projects.wordle.ui.theme.gray200
import com.sinya.projects.wordle.ui.theme.gray300
import com.sinya.projects.wordle.ui.theme.gray600
import com.sinya.projects.wordle.ui.theme.green400
import com.sinya.projects.wordle.ui.theme.green600
import com.sinya.projects.wordle.ui.theme.white
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    themeViewModel: ThemeViewModel,
    localeViewModel: LocaleViewModel,
    navController: NavController
) {
    val context = LocalContext.current

    val isDark by themeViewModel.isDarkMode.collectAsState()
    val lang by localeViewModel.language.collectAsState()
    val codeKeyboard by AppDataStore.getKeyboardMode(context).collectAsState(initial = 0)
    val confettiEnabled by AppDataStore.getConfettiMode(context).collectAsState(initial = false)
    val ratingModeEnabled by AppDataStore.getRatingWordMode(context).collectAsState(initial = false)

    val currentLang = AppLanguages.getByCode(lang)
    val currentTheme = AppThemes.getByCode(isDark)
    val currentKeyboard = AppKeyboards.getByCode(codeKeyboard)
    val coroutineScope = rememberCoroutineScope()

    Column(
        Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 7.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        Header(stringResource(R.string.settings_screen), false, navController)
        Spacer(Modifier.height(0.dp))
        CardColumn {
            RowSettingLink(
                stringResource(R.string.change_lang),
                currentLang ?: "",
                R.drawable.set_lang,
                R.drawable.arrow
            ) { navController.navigate("language") }
            RowSettingLink(
                stringResource(R.string.change_theme),
                currentTheme?.let { stringResource(it.nameRes) } ?: "",
                if (!isDark) R.drawable.set_light else R.drawable.set_night,
                R.drawable.arrow
            ) { navController.navigate("themeMode") }
            RowSettingLink(
                stringResource(R.string.guide),
                stringResource(R.string.start),
                R.drawable.set_guide,
                R.drawable.arrow
            ) {}
        }
        CardColumn {
            RowSettingSwitch(stringResource(R.string.music), R.drawable.set_music, false) {}
            RowSettingSwitch(stringResource(R.string.sounds), R.drawable.set_sound, false) {}
            RowSettingSwitch(stringResource(R.string.vibration), R.drawable.set_vibro, false) {}
        }
        CardColumn {
            RowSettingSwitch(stringResource(R.string.confetti), R.drawable.set_confetti, confettiEnabled) {
                coroutineScope.launch {
                    AppDataStore.setConfettiMode(context, it)
                }
            }
            RowSettingSwitch(stringResource(R.string.abuse_words), R.drawable.set_abuse, ratingModeEnabled) {
                coroutineScope.launch {
                    AppDataStore.setRatingWordMode(context, it)
                }
            }
            RowSettingLink(
                stringResource(R.string.change_keyboard),
                currentKeyboard?.let { stringResource(it.modeName) } ?: "",
                R.drawable.set_delete,
                R.drawable.arrow) { navController.navigate("keyboardMode") }
        }
        CardColumn {
            RowSettingLink(
                stringResource(R.string.send_to_support),
                "",
                R.drawable.set_support,
                R.drawable.arrow_diagonal
            ) { sendSupportEmail(context) }
        }
        AppVersionInfo()
    }
}

@Composable
fun RowSettingLink(
    title: String,
    mode: String,
    @DrawableRes icon: Int,
    @DrawableRes icon2: Int,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            Modifier.weight(0.7f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                Modifier
                    .padding(end = 9.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(color = green600)
                    .scale(0.75f),
                colorFilter = ColorFilter.tint(white)
            )
            Text(
                title,
                fontSize = 15.sp,
                color = gray600,
                style = WordleTypography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                mode,
                fontSize = 13.sp,
                color = gray300,
                modifier = Modifier.padding(end = 4.dp),
                style = WordleTypography.bodyMedium
            )
            Image(
                painter = painterResource(icon2),
                contentDescription = null,
                Modifier.size(15.dp),
                colorFilter = ColorFilter.tint(color = gray200)
            )
        }
    }
}

@Composable
fun RowSettingSwitch(
    title: String,
    @DrawableRes icon: Int,
    isChecked: Boolean,
    onClick: (Boolean) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 9.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(color = green600)
                    .scale(0.75f),
                colorFilter = ColorFilter.tint(white)
            )
            Text(title, fontSize = 15.sp, color = gray600, style = WordleTypography.bodyMedium)
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onClick,
            Modifier
                .size(28.dp, 20.dp)
                .scale(0.73f),
            colors = SwitchDefaults.colors(
                checkedThumbColor = green600,
                checkedTrackColor = green400, // Цвет трека в включенном состоянии
                uncheckedThumbColor = gray100, // Цвет кружка в выключенном состоянии
                uncheckedTrackColor = gray200, // Цвет трека в выключенном состоянии
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}

@Composable
fun CardColumn(body: @Composable ColumnScope.() -> Unit) {
    Card(
        Modifier
            .fillMaxWidth()
            .background(color = WordleColor.colors.backgroundCard, RoundedCornerShape(12.dp))
            .shadow(elevation = 5.dp, spotColor = gray600, shape = RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier
                .background(color = WordleColor.colors.backgroundCard)
                .padding(vertical = 8.dp),
            content = body
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
    Text(
        "${stringResource(R.string.version)}: $versionName ($versionCode)",
        fontSize = 14.sp,
        color = Color.White,
        style = WordleTypography.bodyMedium
    )
}