package com.sinya.projects.wordle.screen.language

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.domain.model.data.LangItem
import com.sinya.projects.wordle.ui.features.CheckedIcon
import com.sinya.projects.wordle.ui.features.Header
import com.sinya.projects.wordle.ui.theme.WordleColor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun LanguageScreen(
    navigateToBackStack: () -> Unit,
    lang: StateFlow<String>,
    languages: List<LangItem>,
    changeLang: (String) -> Unit,

    ) {
    val currentLang by lang.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .background(color = WordleColor.colors.background)
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 7.dp)
    ) {
        Header(
            title = stringResource(R.string.change_lang),
            trashVisible = false,
            navigateTo = navigateToBackStack
        )
        LazyColumn {
            items(languages.size) { index ->
                if (index == 0) {
                    Spacer(Modifier.height(20.dp))
                }
                LanguageItem(
                    nativeName = languages[index].nativeName,
                    englishName = languages[index].englishName,
                    isSelected = languages[index].code == currentLang,
                    onClick = { changeLang(languages[index].code) }
                )
                if (index < languages.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(0.82f),
                        color = WordleColor.colors.textCardSecondary,
                        thickness = 1.dp
                    )
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
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = nativeName,
                style = MaterialTheme.typography.bodyLarge,
                color = WordleColor.colors.textPrimary
            )
            Text(
                text = englishName,
                style = MaterialTheme.typography.bodySmall,
                color = WordleColor.colors.textCardSecondary

            )
        }
        CheckedIcon(isSelected)
    }
}

@Preview
@Composable
private fun LanguageScreenPreview() {
    LanguageScreen(
        navigateToBackStack = { },
        lang = MutableStateFlow("ru"),
        languages = AppLanguages.supported,
        changeLang = { },
    )
}