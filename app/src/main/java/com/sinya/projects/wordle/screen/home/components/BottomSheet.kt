package com.sinya.projects.wordle.screen.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.ui.features.RoundedButton
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(onClickGame: (ScreenRoute) -> Unit, onDismissRequest: () -> Unit, mode: Int) {
    val sheetState = rememberModalBottomSheetState()

    val wordSizes = listOf("4", "5", "6", "7", "8", "9", "10", "11")
    val langMode = listOf(R.string.russian, R.string.english)
    val gameMode = listOf(R.string.classic_mode, R.string.hard_mode_horizontal)

    val lang = remember { mutableIntStateOf(langMode[0]) }
    val selectedSize = remember { mutableStateOf(wordSizes[1]) }
    val modes = remember { mutableIntStateOf(gameMode[mode]) }

    ModalBottomSheet(
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = WordyColor.colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp, start = 30.dp, end = 30.dp, bottom = 43.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextSheet(stringResource(R.string.word_size))
                OptionSelectorRow(
                    items = wordSizes,
                    selected = selectedSize.value,
                    onSelected = { selectedSize.value = it },
                    isCircle = true,
                    label = { it })

                TextSheet(stringResource(R.string.language_of_game))
                OptionSelectorRow(
                    items = langMode,
                    selected = lang.intValue,
                    onSelected = { lang.intValue = it },
                    label = { id -> stringResource(id) }
                )

                TextSheet(stringResource(R.string.mode_of_game))
                OptionSelectorRow(
                    items = gameMode,
                    selected = modes.intValue,
                    onSelected = { modes.intValue = it },
                    label = { id -> stringResource(id) })
                Spacer(Modifier.height(3.dp))

                val gameModeValue = if (modes.intValue == R.string.classic_mode) 0 else 1
                val languageCode = if (lang.intValue == R.string.russian) "ru" else "en"

                val coroutineScope = rememberCoroutineScope()
                RoundedButton(
                    modifier = Modifier.fillMaxWidth(0.7f),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = WordyColor.colors.backgroundActiveBtnMkI,
                        containerColor = WordyColor.colors.textForActiveBtnMkI
                    ),
                    contentPadding = PaddingValues(vertical = 0.dp, horizontal = 10.dp),
                    onClick = {
                        coroutineScope.launch {
                            sheetState.hide()
                            onClickGame(
                                ScreenRoute.Game(
                                    mode = gameModeValue,
                                    wordLength = selectedSize.value.toInt(),
                                    lang = languageCode,
                                )
                            )
                        }
                    },
                ) {
                    Text("Начать", fontSize = 14.sp, style = WordyTypography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun <T> OptionSelectorRow(
    items: List<T>,
    selected: T,
    onSelected: (T) -> Unit,
    isCircle: Boolean = false,
    label: @Composable (T) -> String
) {
    if (isCircle) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items) { item ->
                RoundedButton(
                    elevation = 0,
                    modifier = Modifier.size(37.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selected == item) WordyColor.colors.backgroundActiveBtnMkI else WordyColor.colors.backgroundPassiveBtn,
                        contentColor = WordyColor.colors.textForActiveBtnMkI
                    ),
                    contentPadding = PaddingValues(vertical = 0.dp, horizontal = 10.dp),
                    onClick = { onSelected(item) },
                ) {
                    Text(label(item), fontSize = 14.sp, style = WordyTypography.bodyMedium)
                }
            }
        }
    } else {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items.forEach { item ->
                RoundedButton(
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selected == item) WordyColor.colors.backgroundActiveBtnMkI else WordyColor.colors.backgroundPassiveBtn,
                        contentColor = WordyColor.colors.textForActiveBtnMkI
                    ),
                    contentPadding = PaddingValues(vertical = 0.dp, horizontal = 10.dp),
                    onClick = { onSelected(item) },
                ) {
                    Text(label(item), fontSize = 14.sp, style = WordyTypography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun TextSheet(text: String) {
    Text(
        text,
        textAlign = TextAlign.Center,
        style = WordyTypography.titleLarge,
        fontSize = 18.sp,
        color = WordyColor.colors.textPrimary
    )
}
