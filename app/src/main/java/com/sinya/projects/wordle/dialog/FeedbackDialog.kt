package com.sinya.projects.wordle.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.sinya.projects.wordle.ui.components.CustomTextField
import com.sinya.projects.wordle.ui.theme.WordleShapes
import com.sinya.projects.wordle.ui.theme.WordleTypography
import com.sinya.projects.wordle.ui.theme.gray600
import com.sinya.projects.wordle.ui.theme.gray800
import com.sinya.projects.wordle.ui.theme.green600
import com.sinya.projects.wordle.ui.theme.green800
import com.sinya.projects.wordle.ui.theme.white

@Composable
fun ShowDialogWindow(show: Boolean, onDismiss: () -> Unit) {
    var address by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var letters by remember { mutableStateOf("") }
    val modifier = Modifier
        .shadow(spotColor = green600, shape = WordleShapes.medium, elevation = 3.dp)
        .fillMaxWidth()
        .background(white, WordleShapes.large)
        .border(1.5.dp, green800, WordleShapes.large)
        .padding(horizontal = 12.dp, vertical = 10.dp) // Минимальный отступ

    if (show) {
        Dialog(onDismissRequest = onDismiss) {
            AnimatedVisibility(
                visible = show
            ) {
                Column(
                    Modifier
                        .background(color = gray600, shape = WordleShapes.large)
                        .padding(horizontal = 25.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(17.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Дружеский вызов", color = white, style = WordleTypography.titleLarge, fontSize = 18.sp)
                    CustomTextField(
                        value = address,
                        onValueChange = { address = it },
                        placeholder = "E-mail",
                        modifier = modifier
                    )
                    CustomTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = "Имя",
                        modifier = modifier
                    )
                    CustomTextField(
                        value = letters,
                        onValueChange = { letters = it },
                        placeholder = "Сообщение",
                        modifier,
                        singleLine = false,
                        maxLines = 4,
                        minLines = 4
                    )
                    Button(
                        modifier = Modifier.padding(vertical = 0.dp).fillMaxWidth(0.6f).shadow(shape = CircleShape,elevation = 1.dp, spotColor = gray800),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = green800,
                            contentColor = white
                        ),
                        onClick = onDismiss,
                        contentPadding = PaddingValues(vertical = 7.dp)
                    ) {
                        Text("Отправить", fontSize = 15.sp, style = WordleTypography.bodyMedium)
                    }
                }
            }
        }
    }
}