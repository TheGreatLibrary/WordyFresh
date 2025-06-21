package com.sinya.projects.wordle.screen.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import com.sinya.projects.wordle.ui.features.CheckedIcon
import com.sinya.projects.wordle.ui.features.Header
import com.sinya.projects.wordle.ui.theme.WordleColor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ThemeScreen(
    navigateToBackStack: () -> Unit,
    isDark: StateFlow<Boolean>,
    themes: List<ThemeItem>,
    toggleTheme: (Boolean) -> Unit,
) {
    val currentIsDark by isDark.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 7.dp)
    ) {
        Header(stringResource(R.string.change_theme), false, navigateToBackStack)
        LazyColumn {
            items(themes.size) { index ->
                if (index == 0) {
                    Spacer(Modifier.height(20.dp))
                }
                ThemeModeItem(
                    nativeName = stringResource(themes[index].nameRes),
                    isSelected = themes[index].isDark == currentIsDark,
                    onClick = { toggleTheme(themes[index].isDark) }
                )
                if (index < themes.lastIndex) {
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

@Preview(showBackground = true)
@Composable
private fun ThemeScreenPreview() {
    ThemeScreen(
        navigateToBackStack = { },
        isDark = MutableStateFlow(true),
        themes = AppThemes.supported,
        toggleTheme = { },
    )
}

@Composable
fun ThemeModeItem(
    nativeName: String,
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
        Box(
            modifier = Modifier.height(25.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = nativeName,
                style = MaterialTheme.typography.bodyLarge,
                color = WordleColor.colors.textPrimary
            )
        }
        CheckedIcon(isSelected)
    }
}