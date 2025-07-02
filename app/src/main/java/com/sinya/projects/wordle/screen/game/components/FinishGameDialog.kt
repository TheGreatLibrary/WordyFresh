package com.sinya.projects.wordle.screen.game.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.screen.game.GameUiEvent
import com.sinya.projects.wordle.screen.game.GameUiState
import com.sinya.projects.wordle.ui.features.RoundedButton
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyShapes
import com.sinya.projects.wordle.ui.theme.WordyTypography
import java.util.Locale

@Composable
fun FinishGameDialog(
    state: GameUiState,
    onEvent: (GameUiEvent) -> Unit = { }
) {
    Dialog(onDismissRequest = { onEvent(GameUiEvent.ShowFinishDialog(show = false)) }) {
        AnimatedVisibility(true) {
            val context = LocalContext.current
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(
                        color = WordyColor.colors.background,
                        shape = WordyShapes.large
                    )
                    .padding(horizontal = 12.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(state.result),
                    color = WordyColor.colors.textPrimary,
                    style = WordyTypography.titleLarge,
                    fontSize = 22.sp
                )
                Text(
                    text = stringResource(R.string.hiddent_word),
                    color = WordyColor.colors.textPrimary,
                    style = WordyTypography.bodyMedium,
                    fontSize = 14.sp
                )
                Text(
                    state.hiddenWord,
                    color = WordyColor.colors.textFinishHiddenWord,
                    style = WordyTypography.titleLarge,
                    fontSize = 32.sp,
                    modifier = Modifier
                        .background(WordyColor.colors.backgroundFinishHiddenWord, shape = RoundedCornerShape(8.dp))
                        .padding(10.dp)
                )
                Text(
                    text = stringResource(R.string.parsing_word),
                    color = WordyColor.colors.textLinkColor,
                    style = WordyTypography.bodyMedium,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.clickable {
                        val url = "https://academic.ru/searchall.php?SWord=${
                            state.hiddenWord.lowercase(Locale.ROOT)
                        }"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    } // заменить на отдельный метод (из словаря)
                )
                RoundedButton(
                    modifier = Modifier.fillMaxWidth(0.7f),
                    colors = ButtonDefaults.buttonColors(containerColor = WordyColor.colors.backgroundActiveBtnMkI),
                    contentPadding = PaddingValues(vertical = 0.dp, horizontal = 10.dp),
                    onClick = { onEvent(GameUiEvent.ReloadGame) },
                ) {
                    Text(
                        text = stringResource(R.string.new_game),
                        color = WordyColor.colors.textForActiveBtnMkI,
                        style = WordyTypography.bodyMedium,
                        fontSize = 18.sp
                    )
                }
                Text(
                    text = stringResource(R.string.put_enter_to_play),
                    color = WordyColor.colors.textPrimary,
                    style = WordyTypography.bodyMedium,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}