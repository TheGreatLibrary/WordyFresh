package com.sinya.projects.wordle.dialog.friend_dialog

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sinya.projects.wordle.data.local.database.AppDatabase
import com.sinya.projects.wordle.ui.components.CustomTextField
import com.sinya.projects.wordle.ui.theme.WordleColor
import com.sinya.projects.wordle.ui.theme.WordleShapes
import com.sinya.projects.wordle.ui.theme.WordleTypography
import com.sinya.projects.wordle.ui.theme.gray600
import com.sinya.projects.wordle.ui.theme.gray800
import com.sinya.projects.wordle.ui.theme.green600
import com.sinya.projects.wordle.ui.theme.green800
import com.sinya.projects.wordle.ui.theme.white

@Composable
fun FriendModeDialog(navController: NavController, show: Boolean, onDismiss: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    val viewModel: FriendViewModel = viewModel(
        factory = FriendViewModel.provideFactory(remember { AppDatabase.getInstance(context) }.wordDao())
    )

    LaunchedEffect(Unit) {
        viewModel.copyRequest
            .flowWithLifecycle(lifecycleOwner.lifecycle)
            .collect { cipher ->
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Шифр слова", cipher)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Шифр скопирован в буфер обмена!", Toast.LENGTH_SHORT).show()
            }
    }

    if (show) {
        Dialog(onDismissRequest = onDismiss) {
            AnimatedVisibility(visible = show) {
                Column(
                    Modifier
                        .background(color = gray600, shape = WordleShapes.large)
                        .padding(horizontal = 25.dp, vertical = 25.dp),
                    verticalArrangement = Arrangement.spacedBy(17.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Дружеский вызов",
                        color = white,
                        style = WordleTypography.titleLarge,
                        fontSize = 18.sp
                    )

                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        contentColor = white,
                        indicator = { tabPositions ->
                            SecondaryIndicator(
                                Modifier
                                    .tabIndicatorOffset(tabPositions[selectedTab])
                                    .height(2.dp),
                                color = green600
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = {
                                selectedTab = 0
                                viewModel.onHiddenPlaceChange("")
                                      },
                            modifier = Modifier
                                .weight(1f)
                                .padding(0.dp),
                            selectedContentColor = green600,
                            unselectedContentColor = white,
                            text = { Text("Загадать", style = WordleTypography.bodyMedium, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 0.dp)) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = {
                                selectedTab = 1
                                viewModel.onGuessedWordChange("")
                                      },
                            modifier = Modifier
                                .weight(1f)
                                .padding(0.dp),
                            selectedContentColor = green600,
                            unselectedContentColor = white,
                            text = { Text("Отгадать", style = WordleTypography.bodyMedium, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 0.dp)) }
                        )
                    }
                    GetTabContent(
                        navController,
                        selectedTab,
                        viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun GetTabContent(
    navController: NavController,
    selectedTab: Int,
    viewModel: FriendViewModel,
) {
    val modifier = Modifier
        .shadow(spotColor = gray600, shape = WordleShapes.medium, elevation = 3.dp)
        .fillMaxWidth()
        .background(white, WordleShapes.large)
        .padding(horizontal = 12.dp, vertical = 8.dp)


    when (selectedTab) {
        0 -> WordInputSection(
            title = "Брось вызов другу - загадай ему слово от 4 до 11 букв:",
            textFieldValue = viewModel.hiddenPlace,
            onTextFieldChange = viewModel::onHiddenPlaceChange,
            buttonText = "Скопировать шифр",
            modifier,
            onButtonClick = { viewModel.requestCopyCipher() },
            viewModel
        )

        1 -> WordInputSection(
            title = "Введи шифр ниже, чтобы отгадать слово от друга:",
            textFieldValue = viewModel.guessedWord,
            onTextFieldChange = viewModel::onGuessedWordChange,
            buttonText = "Отгадать слово",
            modifier,
            onButtonClick = { viewModel.navigateToGame(navController) },
            viewModel
        )
    }
}



@Composable
fun WordInputSection(
    title: String,
    textFieldValue: String,
    onTextFieldChange: (String) -> Unit,
    buttonText: String,
    modifier: Modifier,
    onButtonClick: () -> Unit,
    viewModel: FriendViewModel
) {
    Text(
        title,
        color = white,
        fontSize = 14.sp,
        style = WordleTypography.bodyMedium
    )
    CustomTextField(
        value = textFieldValue,
        onValueChange = onTextFieldChange,
        placeholder = "Введите здесь!",
        modifier
    )
    Text(
        text = viewModel.errorText ?: "",
        color = WordleColor.colors.secondary,
        modifier = Modifier.padding(3.dp)
    )
    Button(
        modifier = Modifier
            .fillMaxWidth(0.75f)
            .shadow(shape = CircleShape, elevation = 1.dp, spotColor = gray800),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = green800,
            contentColor = white
        ),
        onClick = onButtonClick,
        contentPadding = PaddingValues(vertical = 7.dp, horizontal = 10.dp)
    ) {
        Text(buttonText, fontSize = 15.sp, style = WordleTypography.bodyMedium)
    }
}