package com.sinya.projects.wordle.presentation.settings.components

import android.os.Build
import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun AppVersionInfo(lang: String? = null) {
    val context = LocalContext.current
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val locale = lang
    val versionName = packageInfo.versionName
    val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        packageInfo.longVersionCode.toInt()
    } else {
        @Suppress("DEPRECATION")
        packageInfo.versionCode
    }
    Log.d("AppVersionInfo", "Recomposed with locale: $locale")
    Text(
        "${stringResource(R.string.version)}: $versionName ($versionCode)",
        fontSize = 14.sp,
        color = WordyColor.colors.textPrimary,
        style = WordyTypography.bodyMedium
    )
}