package com.sinya.projects.wordle.screen.settings.subscreens

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.data.local.datastore.LocaleViewModel
import com.sinya.projects.wordle.domain.model.data.LangItem
import com.sinya.projects.wordle.navigation.Header
import com.sinya.projects.wordle.ui.theme.WordleColor
import kotlinx.coroutines.delay

@Composable
fun LanguageScreen(localeViewModel: LocaleViewModel, navController: NavController) {
    val context = LocalContext.current
    val lang by localeViewModel.language.collectAsState()

    val supportedLanguages = listOf(
        LangItem("en", "English", "English"),
        LangItem("ru", "Русский", "Russian"),
    )

    LaunchedEffect(Unit) {
        localeViewModel.languageChanged.collect {
            delay(150)
            (context as? Activity)?.recreate()
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(color = WordleColor.colors.background)
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 7.dp)
    ) {
        Header(stringResource(R.string.change_lang), false, navController)
        LazyColumn {
            items(supportedLanguages.size) { index ->
                LanguageItem(
                    nativeName = supportedLanguages[index].nativeName,
                    englishName = supportedLanguages[index].englishName,
                    isSelected = supportedLanguages[index].code == lang,
                    onClick = { localeViewModel.changeLanguage(supportedLanguages[index].code) }
                )
                if (index < supportedLanguages.lastIndex) {
                    HorizontalDivider(modifier = Modifier.fillMaxWidth(0.82f), color = Color.LightGray, thickness = 1.dp)
                }
            }
        }
    }
}

@Composable
fun LanguageItem(
    nativeName: String,
    englishName: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(text = nativeName, style = MaterialTheme.typography.bodyLarge)
            Text(text = englishName, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        val scale by animateFloatAsState(
            targetValue = if (isSelected) 1f else 0f,
            animationSpec = tween(
                durationMillis = 150,
                easing = FastOutSlowInEasing
            ),
            label = "scaleAnim"
        )

        if (scale > 0f) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                modifier = Modifier.scale(scale)
            )
        }
    }
}