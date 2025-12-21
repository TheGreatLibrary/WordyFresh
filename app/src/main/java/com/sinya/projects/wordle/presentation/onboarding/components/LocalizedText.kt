package com.sinya.projects.wordle.presentation.onboarding.components

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography
import com.sinya.projects.wordle.utils.withLocale
import java.util.Locale

@Composable
fun LocalizedText(
    onNext: () -> Unit,
    changeLocale: (String) -> Unit
) {
    val context = LocalContext.current

    val currentLang = remember {
        context.resources.configuration.locales[0].language
    }
    val nextLang = remember(currentLang) {
        if (currentLang == "en") "ru" else "en"
    }
    val buttonText = remember(nextLang) {
        val nextContext = context.withLocale(Locale(nextLang))
        nextContext.getString(R.string.continue_in_lang)
    }

    TextButton(
        onClick = {
            onNext()
            changeLocale(nextLang)
        }
    ) {
        Text(
            text = buttonText,
            style = WordyTypography.labelSmall,
            color = WordyColor.colors.textPrimary
        )
    }
}

