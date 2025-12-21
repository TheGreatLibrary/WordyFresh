package com.sinya.projects.wordle.presentation.home.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.domain.model.Game
import com.sinya.projects.wordle.ui.features.RoundedButton
import com.sinya.projects.wordle.ui.theme.Montserrat
import com.sinya.projects.wordle.ui.theme.WordyColor
import java.util.concurrent.TimeUnit


@SuppressLint("DefaultLocale")
@Composable
fun ContinueGameButton(
    savedGame: Game,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    RoundedButton(
        modifier = Modifier.fillMaxWidth(0.7f),
        colors = ButtonDefaults.buttonColors(
            containerColor = WordyColor.colors.backgroundActiveBtnMkI
        ),
        contentPadding = PaddingValues(vertical = 5.dp, horizontal = 15.dp),
        onClick = onClick
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.continue_text),
                fontSize = 16.sp,
                color = WordyColor.colors.textForActiveBtnMkI,
                style = TextStyle(
                    lineHeight = 16.sp,
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Medium
                )
            )
            Text(
                text = buildString {
                    append(
                        context.resources.getQuantityString(
                            R.plurals.letters_count,
                            savedGame.length,
                            savedGame.length
                        )
                    )
                    append(" - ")
                    append(
                        String.format(
                            "%02d:%02d",
                            TimeUnit.SECONDS.toMinutes(savedGame.totalSeconds).toInt(),
                            savedGame.totalSeconds % 60
                        )
                    )
                },
                fontSize = 12.sp,
                color = WordyColor.colors.textForActiveBtnMkI,
                style = TextStyle(
                    lineHeight = 12.sp,
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

