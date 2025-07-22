package com.sinya.projects.wordle.ui.features

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.WordyApplication
import com.sinya.projects.wordle.data.supabase.SupabaseService
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyShapes
import com.sinya.projects.wordle.ui.theme.WordyTypography
import io.github.jan.supabase.auth.auth

@Composable
fun Header(
    title: String,
    trashVisible: Boolean = false,
    navigateTo: () -> Unit,
    trashOnClick: () -> Unit = {},
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ImageButton(
            image = R.drawable.arrow_back,
            modifierImage = Modifier.size(32.dp),
            colorFilter = ColorFilter.tint(WordyColor.colors.textPrimary),
            onClick = navigateTo
        )
        Text(
            title,
            fontSize = 24.sp,
            color = WordyColor.colors.textPrimary,
            style = WordyTypography.titleLarge
        )
        Box(
            modifier = Modifier.size(42.dp),
            contentAlignment = Alignment.Center
        ) {
            if (trashVisible) {
                var showDialog by remember { mutableStateOf(false) }

                ImageButton(
                    image = R.drawable.stat_trash,
                    modifierImage = Modifier.size(32.dp),
                    colorFilter = ColorFilter.tint(WordyColor.colors.textPrimary),
                    onClick = { showDialog = true }
                )

                if (showDialog) {
                    TrashAlertDialog(
                        trashOnClick = trashOnClick,
                        onDismissRequest = { showDialog = false },
                    )
                }
            }
        }
    }
}

@Composable
private fun TrashAlertDialog(
    trashOnClick: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    trashOnClick()
                    onDismissRequest()
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = WordyColor.colors.secondary
                )
            ) {
                Text(stringResource(R.string.delete))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = WordyColor.colors.textPrimary,
                )
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        containerColor = WordyColor.colors.background,
        title = {
            Box(
                Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = stringResource(R.string.confirm_delete),
                    style = WordyTypography.bodyLarge,
                    fontSize = 18.sp,
                    color = WordyColor.colors.textPrimary
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
                    style = WordyTypography.bodyMedium,
                    fontSize = 14.sp,
                    color = WordyColor.colors.textPrimary
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = WordyShapes.small
    )
}