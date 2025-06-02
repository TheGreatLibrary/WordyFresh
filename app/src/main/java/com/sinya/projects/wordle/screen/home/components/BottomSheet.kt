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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.ui.features.RoundedButton
import com.sinya.projects.wordle.ui.theme.WordleColor
import com.sinya.projects.wordle.ui.theme.WordleTypography
import com.sinya.projects.wordle.ui.theme.gray800
import com.sinya.projects.wordle.ui.theme.green800
import com.sinya.projects.wordle.ui.theme.white

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(onClickGame: (ScreenRoute) -> Unit, onDismissRequest: () -> Unit, mode: Int) {
    val sheetState = rememberModalBottomSheetState()

    val wordSizes = listOf("4", "5", "6", "7", "8", "9", "10", "11")
    val langMode = listOf("Русский", "Английский")
    val gameMode = listOf("Классика", "Сложный")

    val lang = remember { mutableStateOf(langMode[0]) }
    val selectedSize = remember { mutableStateOf(wordSizes[1]) }
    val modes = remember { mutableStateOf(gameMode[mode]) }

    ModalBottomSheet(
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = WordleColor.colors.background
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
                TextSheet("Размер слова")
                OptionSelectorRow(wordSizes, selectedSize.value, { item -> selectedSize.value = item }, true)

                TextSheet("Язык игры")
                OptionSelectorRow(langMode, lang.value, { item -> lang.value = item })

                TextSheet("Режим игры")
                OptionSelectorRow(gameMode, modes.value, { item -> modes.value = item })
                Spacer(Modifier.height(3.dp))

                val gameModeValue = if (modes.value == "Классика") 0 else 1
                val languageCode = if (lang.value == "Русский") "ru" else "en"
                val navRoute = "game/$gameModeValue/${selectedSize.value}/$languageCode/"

                RoundedButton(
                    modifier = Modifier.fillMaxWidth(0.7f),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = WordleColor.colors.backgroundActiveBtnMkI,
                        containerColor = WordleColor.colors.textForActiveBtnMkI
                    ),
                    contentPadding = PaddingValues(vertical = 0.dp, horizontal = 10.dp),
                    onClick = { onClickGame(
                        ScreenRoute.Game(
                            mode = gameModeValue,
                            wordLength = selectedSize.value.toInt(),
                            lang = languageCode,
                            )
                        )
                    },
                ) {
                    Text("Начать", fontSize = 14.sp, style = WordleTypography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun OptionSelectorRow(
    items: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    isCircle: Boolean = false
) {
    if (isCircle) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items) { item ->
                RoundedButton(
                    elevation = 0,
                    modifier = Modifier.size(37.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selected == item) WordleColor.colors.backgroundActiveBtnMkI else WordleColor.colors.backgroundPassiveBtn,
                        contentColor = WordleColor.colors.textForActiveBtnMkI
                    ),
                    contentPadding = PaddingValues(vertical = 0.dp, horizontal = 10.dp),
                    onClick = { onSelected(item) },
                ) {
                    Text(text = item, fontSize = 14.sp, style = WordleTypography.bodyMedium)
                }
            }
        }
    } else {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items.forEach { item ->
                RoundedButton(
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selected == item) WordleColor.colors.backgroundActiveBtnMkI else WordleColor.colors.backgroundPassiveBtn,
                        contentColor = WordleColor.colors.textForActiveBtnMkI
                    ),
                    contentPadding = PaddingValues(vertical = 0.dp, horizontal = 10.dp),
                    onClick = { onSelected(item) },
                ) {
                    Text(item, fontSize = 14.sp, style = WordleTypography.bodyMedium)
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
        style = WordleTypography.titleLarge,
        fontSize = 18.sp,
        color = WordleColor.colors.textPrimary
    )
}
