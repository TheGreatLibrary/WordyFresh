package com.sinya.projects.wordle.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sinya.projects.wordle.ui.theme.WordleColor
import com.sinya.projects.wordle.ui.theme.WordleShapes
import com.sinya.projects.wordle.ui.theme.WordleTypography
import com.sinya.projects.wordle.ui.theme.gray600
import com.sinya.projects.wordle.ui.theme.gray800
import com.sinya.projects.wordle.ui.theme.green800
import com.sinya.projects.wordle.ui.theme.white


@Composable
fun ButtonSheet(modifier: Modifier, text: String, select: MutableState<String>) {
    Button(
        modifier = modifier
            .shadow(5.dp, spotColor = gray800, shape = WordleShapes.extraLarge)
            .clip(WordleShapes.extraLarge),
        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (select.value == text) green800 else gray800,
            contentColor = white
        ),
        onClick = { select.value = text },
    ) {
        Text(text, fontSize = 14.sp, style = WordleTypography.bodyMedium)
    }
}

@Composable
fun TextSheet(text: String) {
    Text(
        text,
        textAlign = TextAlign.Center,
        style = WordleTypography.titleLarge,
        fontSize = 18.sp,
        color = WordleColor.colors.textColorMkII
    )
}

@Composable
fun SelectionRow(options: List<String>, selected: MutableState<String>, modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            ButtonSheet(Modifier.weight(1f), option, selected)
        }
    }
}

@Composable
fun SizeSelectionRow(sizes: List<String>, selectedSize: MutableState<String>) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(sizes) { size ->
            Button(
                modifier = Modifier
                    .size(37.dp)
                    .shadow(5.dp, spotColor = gray800, shape = WordleShapes.extraLarge)
                    .clip(WordleShapes.extraLarge),
                contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedSize.value == size) green800 else gray800,
                    contentColor = white
                ),
                onClick = { selectedSize.value = size },
            ) {
                Text(text = size, fontSize = 14.sp, style = WordleTypography.bodyMedium)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetDialog(navController: NavController, onDismissRequest: () -> Unit, mode: Int) {
    val sheetState = rememberModalBottomSheetState()

    val wordSizes = listOf("4", "5", "6", "7", "8", "9", "10", "11")
    val selectedSize = remember { mutableStateOf(wordSizes[1]) } // длина слова
    val langMode = listOf("Русский", "Английский")
    val lang = remember { mutableStateOf(langMode[0]) } // язык игры
    val gameMode = listOf("Классика", "Сложный")
    val modes = remember { mutableStateOf(gameMode[mode]) } // режим игры

    ModalBottomSheet(
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = gray600
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp, start = 30.dp, end = 30.dp, bottom = 43.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextSheet("Размер слова")
                SizeSelectionRow(wordSizes, selectedSize)

                TextSheet("Язык игры")
                SelectionRow(langMode, lang)

                TextSheet("Режим игры")
                SelectionRow(gameMode, modes)

                val gameModeValue = if (modes.value == "Классика") 0 else 1
                val languageCode = if (lang.value == "Русский") "ru" else "en"
                val navRoute = "game/$gameModeValue/${selectedSize.value}/$languageCode/"

                Button(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth(0.7f)
                        .shadow(5.dp, spotColor = gray800, shape = WordleShapes.extraLarge)
                        .clip(WordleShapes.large),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = green800,
                        containerColor = white
                    ),
                    onClick = { navController.navigate(navRoute) },
                ) {
                    Text("Начать", fontSize = 14.sp, style = WordleTypography.bodyMedium)
                }
            }
        }
    }
}
