package com.sinya.projects.wordle.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.ui.components.ImageButton
import com.sinya.projects.wordle.ui.theme.WordleColor
import com.sinya.projects.wordle.ui.theme.WordleColor.colors
import com.sinya.projects.wordle.ui.theme.WordleTypography

@Composable
fun Header(name: String, trashVisible: Boolean, navController: NavController) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ImageButton(
            R.drawable.arrow_back,
            modifier = Modifier.size(32.dp),
            colorFilter = ColorFilter.tint(WordleColor.colors.backgroundCard)
        ) { navController.popBackStack() }
        Text(name, fontSize = 24.sp, color = WordleColor.colors.backgroundCard, style = WordleTypography.titleLarge)
        Box(Modifier.size(32.dp)) {
            if (trashVisible) {
                var showDialog by remember { mutableStateOf(false) }

                ImageButton(
                    R.drawable.stat_trash, modifier = Modifier
                        .size(32.dp)
                        .scale(0.9f),
                    colorFilter = ColorFilter.tint(WordleColor.colors.backgroundCard)


                ) {
                    showDialog = true
                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
//                                clearStats()
                                    showDialog = false
                                }, colors = ButtonDefaults.textButtonColors(
                                    contentColor = colors.secondary
                                )
                            ) {
                                Text(stringResource(R.string.delete))
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showDialog = false },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = colors.background,
                                )
                            ) {
                                Text(stringResource(R.string.cancel))
                            }
                        },
                        containerColor = colors.backgroundCard,
                        title = {
                            Box(
                                Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                Text(
                                    text = stringResource(R.string.confirm_delete),
                                    style = WordleTypography.bodyLarge,
                                    fontSize = 18.sp,
                                    color = colors.background
                                )
                            }
                        },
                        text = {
                            Box(
                                Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    stringResource(R.string.text_dialog_delete_stat),
                                    style = WordleTypography.bodyMedium,
                                    fontSize = 14.sp,
                                    color = colors.background
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

            }
        }
    }
}
