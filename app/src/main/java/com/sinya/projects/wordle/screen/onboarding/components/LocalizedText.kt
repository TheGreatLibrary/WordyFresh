package com.sinya.projects.wordle.screen.onboarding.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.ui.theme.WordleTypography
import com.sinya.projects.wordle.utils.getInitialAppLocale
import com.sinya.projects.wordle.utils.withLocale
import java.util.Locale

@Composable
fun LocalizedText(
    onNext: () -> Unit,
    changeLocale: (String) -> Unit
) {
    val context = LocalContext.current
    val currentLang = context.resources.configuration.locales[0].language
    val nextLang = remember(currentLang) {
        if (currentLang == "en") getInitialAppLocale(context) else "en"
    }
    val nextContext = remember(nextLang) { context.withLocale(Locale(nextLang)) }
    val buttonText = nextContext.getString(R.string.continue_in_lang)

    Text(
        text = buttonText,
        style = WordleTypography.labelSmall,
        modifier = Modifier.clickable {
            onNext()
                changeLocale(nextLang)
        }
    )
}

