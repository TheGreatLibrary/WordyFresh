package com.sinya.projects.wordle.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.domain.enums.GameMode
import com.sinya.projects.wordle.domain.enums.TypeLanguages
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.ui.features.CustomModalSheet
import com.sinya.projects.wordle.ui.features.RoundedButton
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun NewGameBottomSheet(
    onClickGame: (ScreenRoute) -> Unit,
    onDismissRequest: () -> Unit,
    initialMode: GameMode
) {
    var selectedWordSize by remember { mutableIntStateOf(5) }
    var selectedLang by remember { mutableStateOf(TypeLanguages.RU) }
    var selectedGameMode by remember { mutableStateOf(initialMode) }

    CustomModalSheet(onDismissRequest) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp, bottom = 45.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SheetSection(title = stringResource(R.string.word_size)) {
                WordSizeSelector(
                    selected = selectedWordSize,
                    onSelected = { selectedWordSize = it }
                )
            }
            SheetSection(title = stringResource(R.string.language_of_game)) {
                LanguageSelector(
                    selected = selectedLang,
                    onSelected = { selectedLang = it }
                )
            }
            SheetSection(title = stringResource(R.string.mode_of_game)) {
                GameModeSelector(
                    selected = selectedGameMode,
                    onSelected = { selectedGameMode = it }
                )
            }

            RoundedButton(
                modifier = Modifier.fillMaxWidth(0.7f),
                colors = ButtonDefaults.buttonColors(
                    contentColor = WordyColor.colors.backgroundActiveBtnMkI,
                    containerColor = WordyColor.colors.textForActiveBtnMkI
                ),
                contentPadding = PaddingValues(vertical = 0.dp, horizontal = 10.dp),
                onClick = {
                    onClickGame(
                        ScreenRoute.Game(
                            mode = selectedGameMode.id,
                            wordLength = selectedWordSize,
                            lang = selectedLang.code,
                        )
                    )
                }
            ) {
                Text(
                    text = stringResource(R.string.start_game),
                    fontSize = 14.sp,
                    style = WordyTypography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun WordSizeSelector(
    selected: Int,
    onSelected: (Int) -> Unit
) {
    val sizes = remember { (4..11).toList() }

    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(sizes) { size ->
            SelectableCircleButton(
                text = size.toString(),
                isSelected = selected == size,
                onClick = { onSelected(size) }
            )
        }
    }
}

@Composable
private fun LanguageSelector(
    selected: TypeLanguages,
    onSelected: (TypeLanguages) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TypeLanguages.entries.forEach { lang ->
            SelectableButton(
                text = stringResource(lang.originName),
                isSelected = selected == lang,
                onClick = { onSelected(lang) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun GameModeSelector(
    selected: GameMode,
    onSelected: (GameMode) -> Unit
) {
    val modes = remember {
        listOf(
            GameMode.NORMAL to R.string.classic_mode,
            GameMode.HARD to R.string.hard_mode_horizontal
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        modes.forEach { (mode, labelRes) ->
            SelectableButton(
                text = stringResource(labelRes),
                isSelected = selected == mode,
                onClick = { onSelected(mode) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SelectableCircleButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    RoundedButton(
        elevation = 0,
        modifier = Modifier.size(37.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) {
                WordyColor.colors.backgroundActiveBtnMkI
            } else {
                WordyColor.colors.backgroundPassiveBtn
            },
            contentColor = WordyColor.colors.textForActiveBtnMkI
        ),
        contentPadding = PaddingValues(0.dp),
        onClick = onClick,
    ) {
        Text(text, fontSize = 14.sp, style = WordyTypography.bodyMedium)
    }
}

@Composable
private fun SelectableButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    RoundedButton(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) {
                WordyColor.colors.backgroundActiveBtnMkI
            } else {
                WordyColor.colors.backgroundPassiveBtn
            },
            contentColor = WordyColor.colors.textForActiveBtnMkI
        ),
        contentPadding = PaddingValues(vertical = 0.dp, horizontal = 10.dp),
        onClick = onClick,
    ) {
        Text(text, fontSize = 14.sp, style = WordyTypography.bodyMedium)
    }
}

@Composable
private fun SheetSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            textAlign = TextAlign.Center,
            style = WordyTypography.titleLarge,
            fontSize = 18.sp,
            color = WordyColor.colors.textPrimary
        )
        content()
    }
}