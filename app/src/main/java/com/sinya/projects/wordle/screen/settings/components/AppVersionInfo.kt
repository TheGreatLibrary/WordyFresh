package com.sinya.projects.wordle.screen.settings.components

import android.os.Build
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.ui.theme.WordleColor
import com.sinya.projects.wordle.ui.theme.WordleTypography

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
        color = WordleColor.colors.textPrimary,
        style = WordleTypography.bodyMedium
    )
}