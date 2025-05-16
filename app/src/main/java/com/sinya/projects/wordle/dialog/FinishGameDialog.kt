package com.sinya.projects.wordle.dialog

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.sinya.projects.wordle.screen.game.GameViewModel
import com.sinya.projects.wordle.ui.theme.WordleColor
import com.sinya.projects.wordle.ui.theme.WordleShapes
import com.sinya.projects.wordle.ui.theme.WordleTypography
import com.sinya.projects.wordle.ui.theme.gray600
import com.sinya.projects.wordle.ui.theme.gray800
import com.sinya.projects.wordle.ui.theme.green600
import com.sinya.projects.wordle.ui.theme.white
import java.util.Locale

@Composable
fun FinishGameDialog(viewModel: GameViewModel, show: MutableState<Boolean>, onDismiss: () -> Unit) {
    if (show.value) {
        Dialog(onDismissRequest = onDismiss) {
            AnimatedVisibility(visible = show.value) {
                val context = LocalContext.current
                Column(
                    Modifier
                        .background(color = gray600, shape = WordleShapes.large)
                        .padding(horizontal = 12.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        viewModel.result,
                        color = white,
                        style = WordleTypography.titleLarge,
                        fontSize = 22.sp
                    )
                    Text(
                        text = "Ответ был:",
                        color = white,
                        style = WordleTypography.bodyMedium,
                        fontSize = 14.sp
                    )
                    Text(
                        viewModel.hiddenWord,
                        color = white,
                        style = WordleTypography.titleLarge,
                        fontSize = 32.sp,
                        modifier = Modifier
                            .background(gray800, shape = RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    )
                    Text(
                        text = "Может вам интересно значение слова?",
                        color = green600,
                        style = WordleTypography.bodyMedium,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.clickable {
                            val url = "https://academic.ru/searchall.php?SWord=${viewModel.hiddenWord.lowercase(Locale.ROOT)}"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent) }
                    )
                    Button(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .shadow(8.dp, spotColor = gray800),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = WordleColor.colors.backgroundBtnMkIII),
                        onClick = { viewModel.reloadGame() },
                        contentPadding = PaddingValues(vertical = 1.dp)
                    ) {
                        Text(
                            text = "Новая игра",
                            color = white,
                            style = WordleTypography.bodyMedium,
                            fontSize = 18.sp
                        )
                    }
                    Text(
                        text = "Или нажмите 'Enter', чтобы начать снова",
                        color = white,
                        style = WordleTypography.bodyMedium,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

//@Composable
//fun GetTabContent(
//    navController: NavController,
//    selectedTab: Int,
//    hiddenPlace: String,
//    guessedWord: String,
//    onHiddenPlaceChange: (String) -> Unit,
//    onGuessedWordChange: (String) -> Unit
//) {
//    val modifier = Modifier
//        .shadow(spotColor = gray600, shape = WordleShapes.medium, elevation = 3.dp)
//        .fillMaxWidth()
//        .background(white, WordleShapes.large)
//        .padding(horizontal = 12.dp, vertical = 8.dp) // Минимальный отступ
//
//
//    when (selectedTab) {
//        0 -> WordInputSection(
//            title = "Брось вызов другу - загадай ему слово от 4 до 11 букв:",
//            textFieldValue = hiddenPlace,
//            onTextFieldChange = onHiddenPlaceChange,
//            buttonText = "Скопировать шифр",
//            modifier,
//            onButtonClick = { /* Действие по копированию скрытого слова */ }
//        )
//
//        1 -> WordInputSection(
//            title = "Введи шифр, чтобы отгадать слово:",
//            textFieldValue = guessedWord,
//            onTextFieldChange = onGuessedWordChange,
//            buttonText = "Отгадать слово",
//            modifier,
//            onButtonClick = { navController.navigate("game/2/${guessedWord.length}/ru/$guessedWord") }
//        )
//    }
//}

//@Composable
//fun WordInputSection(
//    title: String,
//    textFieldValue: String,
//    onTextFieldChange: (String) -> Unit,
//    buttonText: String,
//    modifier: Modifier,
//    onButtonClick: () -> Unit
//) {
//    Text(
//        title,
//        color = white,
//        fontSize = 14.sp,
//        style = WordleTypography.bodyMedium
//    )
//    CustomTextField(
//        value = textFieldValue,
//        onValueChange = onTextFieldChange,
//        placeholder = "Введите здесь!",
//        modifier
//    )
//    Button(
//        modifier = Modifier
//            .fillMaxWidth(0.75f)
//            .shadow(shape = CircleShape, elevation = 1.dp, spotColor = gray800),
//        shape = CircleShape,
//        colors = ButtonDefaults.buttonColors(
//            containerColor = green800,
//            contentColor = white
//        ),
//        onClick = onButtonClick,
//        contentPadding = PaddingValues(vertical = 7.dp, horizontal = 10.dp)
//    ) {
//        Text(buttonText, fontSize = 15.sp, style = WordleTypography.bodyMedium)
//    }
//}