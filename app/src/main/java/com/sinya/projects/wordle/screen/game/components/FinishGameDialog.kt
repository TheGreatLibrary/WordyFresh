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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.screen.game.GameUiEvent
import com.sinya.projects.wordle.screen.game.GameUiState
import com.sinya.projects.wordle.ui.features.RoundedButton
import com.sinya.projects.wordle.ui.theme.WordleColor
import com.sinya.projects.wordle.ui.theme.WordleShapes
import com.sinya.projects.wordle.ui.theme.WordleTypography
import com.sinya.projects.wordle.ui.theme.gray800
import com.sinya.projects.wordle.ui.theme.green600
import java.util.Locale

@Composable
fun FinishGameDialog(
    state: GameUiState,
    onEvent: (GameUiEvent) -> Unit = { }
) {
    Dialog(onDismissRequest = { onEvent(GameUiEvent.GameFinished(state.result)) }) {
        AnimatedVisibility(true) {
            val context = LocalContext.current
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(
                        color = WordleColor.colors.background,
                        shape = WordleShapes.large
                    )
                    .padding(horizontal = 12.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(state.result),
                    color = WordleColor.colors.textPrimary,
                    style = WordleTypography.titleLarge,
                    fontSize = 22.sp
                )
                Text(
                    text = stringResource(R.string.hiddent_word),
                    color = WordleColor.colors.textPrimary,
                    style = WordleTypography.bodyMedium,
                    fontSize = 14.sp
                )
                Text(
                    state.hiddenWord,
                    color = WordleColor.colors.textFinishHiddenWord,
                    style = WordleTypography.titleLarge,
                    fontSize = 32.sp,
                    modifier = Modifier
                        .background(WordleColor.colors.backgroundFinishHiddenWord, shape = RoundedCornerShape(8.dp))
                        .padding(10.dp)
                )
                Text(
                    text = stringResource(R.string.parsing_word),
                    color = WordleColor.colors.textLinkColor,
                    style = WordleTypography.bodyMedium,
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
                    colors = ButtonDefaults.buttonColors(containerColor = WordleColor.colors.backgroundActiveBtnMkI),
                    contentPadding = PaddingValues(vertical = 0.dp, horizontal = 10.dp),
                    onClick = { onEvent(GameUiEvent.ReloadGame) },
                ) {
                    Text(
                        text = stringResource(R.string.new_game),
                        color = WordleColor.colors.textForActiveBtnMkI,
                        style = WordleTypography.bodyMedium,
                        fontSize = 18.sp
                    )
                }
                Text(
                    text = stringResource(R.string.put_enter_to_play),
                    color = WordleColor.colors.textPrimary,
                    style = WordleTypography.bodyMedium,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}