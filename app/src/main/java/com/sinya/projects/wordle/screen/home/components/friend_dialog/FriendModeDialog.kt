package com.sinya.projects.wordle.screen.home.components.friend_dialog

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.WordyApplication
import com.sinya.projects.wordle.data.local.database.AppDatabase
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.ui.features.CustomTextField
import com.sinya.projects.wordle.ui.features.RoundedButton
import com.sinya.projects.wordle.ui.theme.WordleColor
import com.sinya.projects.wordle.ui.theme.WordleShapes
import com.sinya.projects.wordle.ui.theme.WordleTypography
import com.sinya.projects.wordle.ui.theme.gray600
import com.sinya.projects.wordle.ui.theme.gray800
import com.sinya.projects.wordle.ui.theme.green600
import com.sinya.projects.wordle.ui.theme.green800
import com.sinya.projects.wordle.ui.theme.white

@Composable
fun FriendModeDialog(navigateTo: (ScreenRoute) -> Unit, onDismiss: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val db = WordyApplication.database

    val viewModel: FriendViewModel = viewModel(
        factory = FriendViewModel.provideFactory(remember { db }.wordDao())
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

    Dialog(onDismissRequest = onDismiss) {
        AnimatedVisibility(visible = true) {
            Column(
                Modifier
                    .background(color = WordleColor.colors.background, shape = WordleShapes.large)
                    .padding(horizontal = 25.dp, vertical = 25.dp),
                verticalArrangement = Arrangement.spacedBy(17.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(R.string.friend_mode),
                    color = WordleColor.colors.textPrimary,
                    style = WordleTypography.titleLarge,
                    fontSize = 18.sp
                )
                TabRow(
                    selectedTabIndex = viewModel.selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = WordleColor.colors.textPrimary,
                    indicator = { tabPositions ->
                        SecondaryIndicator(
                            Modifier
                                .tabIndicatorOffset(tabPositions[viewModel.selectedTab])
                                .height(2.dp),
                            color = WordleColor.colors.backPrimary
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    CustomTab(
                        selected = viewModel.selectedTab == 0,
                        onClick = {
                            viewModel.selectedTab = 0
                            viewModel.onHiddenPlaceChange("")
                        },
                        modifier = Modifier.weight(1f),
                        text = "Загадать"
                    )
                    CustomTab(
                        selected = viewModel.selectedTab == 1,
                        onClick = {
                            viewModel.selectedTab = 1
                            viewModel.onHiddenPlaceChange("")
                        },
                        modifier = Modifier.weight(1f),
                        text = "Отгадать"
                    )
                }
                GetTabContent(
                    navigateTo,
                    viewModel.selectedTab,
                    viewModel
                )
            }
        }
    }
}

@Composable
private fun CustomTab(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
    text: String,
) {
    Tab(
        selected = selected,
        onClick = onClick,
        modifier = modifier
            .padding(0.dp),
        selectedContentColor = WordleColor.colors.backPrimary,
        unselectedContentColor = WordleColor.colors.textPrimary,
        text = {
            Text(
                text,
                style = WordleTypography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 0.dp)
            )
        }
    )
}

@Composable
private fun GetTabContent(
    navigateTo: (ScreenRoute) -> Unit,
    selectedTab: Int,
    viewModel: FriendViewModel,
) {
    val modifier = Modifier
        .fillMaxWidth()
        .background(white, WordleShapes.large)
        .padding(horizontal = 12.dp, vertical = 10.dp)

    when (selectedTab) {
        0 -> WordInputSection(
            title = stringResource(R.string.put_word),
            textFieldValue = viewModel.hiddenPlace,
            onTextFieldChange = viewModel::onHiddenPlaceChange,
            buttonText = stringResource(R.string.copy_shifr),
            modifier,
            onButtonClick = { viewModel.requestCopyCipher() },
            viewModel.errorText
        )
        1 -> WordInputSection(
            title = stringResource(R.string.put_shifr),
            textFieldValue = viewModel.guessedWord,
            onTextFieldChange = viewModel::onGuessedWordChange,
            buttonText = stringResource(R.string.decode_word),
            modifier,
            onButtonClick = { viewModel.navigateToGame(navigateTo = navigateTo) },
            viewModel.errorText
        )
    }
}


@Composable
private fun WordInputSection(
    title: String,
    textFieldValue: String,
    onTextFieldChange: (String) -> Unit,
    buttonText: String,
    modifier: Modifier,
    onButtonClick: () -> Unit,
    errorText: String?
) {
    Text(
        title,
        color = WordleColor.colors.textPrimary,
        fontSize = 14.sp,
        style = WordleTypography.bodyMedium
    )
    CustomTextField(
        value = textFieldValue,
        onValueChange = onTextFieldChange,
        placeholder = stringResource(R.string.put_here),
        modifier,
        color = WordleColor.colors.primary,
    )
    Text(
        text = errorText ?: "",
        color = WordleColor.colors.secondary,
        modifier = Modifier.padding(3.dp)
    )
    RoundedButton(
        modifier = Modifier
            .fillMaxWidth(0.75f),
        colors = ButtonDefaults.buttonColors(
            containerColor = WordleColor.colors.backgroundActiveBtnMkI,
            contentColor = WordleColor.colors.textForActiveBtnMkI
        ),
        contentPadding = PaddingValues(vertical = 0.dp, horizontal = 10.dp),
        onClick = onButtonClick,
    ) {
        Text(buttonText, fontSize = 15.sp, style = WordleTypography.bodyMedium)
    }
}