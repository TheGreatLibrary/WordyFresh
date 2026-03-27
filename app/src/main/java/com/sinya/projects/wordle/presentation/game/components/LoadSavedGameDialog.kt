package com.sinya.projects.wordle.presentation.game.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun LoadSavedGameDialog(
    checked: Boolean,
    checkBoxToggle: (Boolean) -> Unit,
    onLoadGameClick: () -> Unit,
    onNewGameClick: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    Dialog(
        onDismissRequest = {
            onNewGameClick()
            onDismissRequest()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = WordyColor.colors.background,
                    shape = MaterialTheme.shapes.small
                )
                .padding(vertical = 16.dp, horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.load_saved_game_title_dialog),
                style = WordyTypography.bodyLarge,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = WordyColor.colors.textPrimary
            )
            Text(
                stringResource(R.string.load_saved_game_text_dialog),
                style = WordyTypography.bodyMedium,
                fontSize = 14.sp,
                color = WordyColor.colors.textPrimary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Checkbox(
                    modifier = Modifier
                        .scale(0.8f)
                        .size(20.dp),
                    checked = checked,
                    onCheckedChange = { checkBoxToggle(checked) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = WordyColor.colors.primary,
                        checkmarkColor = WordyColor.colors.textOnColorCard
                    )
                )
                Text(
                    text = stringResource(R.string.dont_take_warning),
                    style = WordyTypography.bodyMedium,
                    fontSize = 12.sp,
                    color = WordyColor.colors.textPrimary
                )
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        onLoadGameClick()
                        onDismissRequest()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = WordyColor.colors.textPrimary,

                    )
                ) {
                    Text(stringResource(R.string.load_saved_game))
                }
                Spacer(Modifier.width(8.dp))
                TextButton(
                    onClick = {
                        onNewGameClick()
                        onDismissRequest()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = WordyColor.colors.primary,
                    )
                ) {
                    Text(stringResource(R.string.new_game))
                }
            }
        }
    }
}