package com.sinya.projects.wordle.presentation.settings.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.domain.enums.TypeThemes
import com.sinya.projects.wordle.ui.features.CardColumn
import com.sinya.projects.wordle.ui.features.RowLink

@Composable
fun AppSettingsCard(
    currentTheme: TypeThemes,
    currentLang: String,
    onThemeClick: (Boolean) -> Unit,
    onLanguageClick: () -> Unit,
    navigateToOnboarding: () -> Unit
) {
    CardColumn {
        RowSwitch(
            title = stringResource(R.string.change_theme),
            icon = if (!currentTheme.isDark) R.drawable.set_light else R.drawable.set_night,
            isChecked = currentTheme.isDark,
            onClick = onThemeClick
        )
        RowLink(
            title = stringResource(R.string.change_lang),
            mode = currentLang,
            icon = R.drawable.set_lang,
            icon2 = R.drawable.arrow,
            navigateTo = onLanguageClick
        )
        RowLink(
            title = stringResource(R.string.guide),
            mode = stringResource(R.string.start),
            icon = R.drawable.set_guide,
            icon2 = R.drawable.arrow,
            navigateTo = navigateToOnboarding
        )
    }
}
