package com.sinya.projects.wordle.screen.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.WordyApplication
import com.sinya.projects.wordle.data.achievement.AchievementTrigger
import com.sinya.projects.wordle.data.achievement.objects.AchievementManager
import com.sinya.projects.wordle.data.local.datastore.AppDataStore
import com.sinya.projects.wordle.screen.keyboard.KeyboardItem
import com.sinya.projects.wordle.screen.theme.ThemeItem
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.screen.keyboard.AppKeyboards
import com.sinya.projects.wordle.screen.language.AppLanguages
import com.sinya.projects.wordle.screen.theme.AppThemes
import com.sinya.projects.wordle.utils.sendSupportEmail
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    navigateToBackStack: () -> Unit,
    navigateTo: (ScreenRoute) -> Unit,
    isDark: StateFlow<Boolean>,
    lang: StateFlow<String>
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val currentIsDark by isDark.collectAsState()
    val locale by lang.collectAsState()
    val codeKeyboard by AppDataStore.getKeyboardMode(context).collectAsState(initial = 0)
    val confettiEnabled by AppDataStore.getConfettiMode(context).collectAsState(initial = false)
    val ratingModeEnabled by AppDataStore.getRatingWordMode(context).collectAsState(initial = false)

    val currentLang = AppLanguages.getByCode(locale)
    val currentTheme = AppThemes.getByCode(currentIsDark)
    val currentKeyboard = AppKeyboards.getByCode(codeKeyboard)

    SettingsScreenView(
        isDark = currentTheme?: ThemeItem(false, R.string.light),
        keyboardMode = currentKeyboard?: KeyboardItem(0, R.string.keyboard_wordle, R.string.keyboard_wordle, R.drawable.keyboard_wordle),
        lang = currentLang,
        confetti = confettiEnabled,
        rating = ratingModeEnabled,
        onConfettiChange = {
            coroutineScope.launch {
                AppDataStore.setConfettiMode(context, it)
            }
        },
        onRatingChange = {
            coroutineScope.launch {
                AppDataStore.setRatingWordMode(context, it)
            }
        },
        navigateToBackStack = navigateToBackStack,
        navigateTo = navigateTo,
        sendEmail = {
            sendSupportEmail(context)
            coroutineScope.launch {
                AchievementManager.onTrigger(AchievementTrigger.SupportMessageSent, WordyApplication.database.loadStats())
            }
        }
    )
}



