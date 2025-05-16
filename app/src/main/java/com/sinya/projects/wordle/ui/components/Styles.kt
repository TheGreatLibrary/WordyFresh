package com.sinya.projects.wordle.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.ui.theme.Montserrat
import com.sinya.projects.wordle.ui.theme.gray400
import com.sinya.projects.wordle.ui.theme.green400
import com.sinya.projects.wordle.ui.theme.green800

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    minLines: Int = 1,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    val customSelectionColors = TextSelectionColors(
        backgroundColor = green400,
        handleColor = green400
    )

    CompositionLocalProvider(LocalTextSelectionColors provides customSelectionColors) {
        Column() {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = singleLine,
                maxLines = maxLines,
                minLines = minLines,
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Medium,
                    color = green800
                ),
                cursorBrush = SolidColor(green800),
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = if (isError) Color.Red else Color.Transparent,
                        shape = RoundedCornerShape(90.dp)
                    )
                    .then(modifier),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = gray400,
                                fontSize = 14.sp,
                                fontFamily = Montserrat,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        innerTextField()
                    }
                }
            )

            if (!errorMessage.isNullOrBlank()) {
                if (isError) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        fontSize = 12.sp,
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Normal
                    )
                }
                else Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun ImageButton(image: Int, modifier: Modifier, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Image(
            painter = painterResource(image),
            contentDescription = null,
            modifier,
        )
    }
}

@Composable
fun RoundedBackgroundText(text: String) {
    Box(
        modifier = Modifier
            .background(color = Color(0x9C257572), shape = RoundedCornerShape(90.dp))
            .padding(horizontal = 30.dp, vertical = 8.dp)
    ) {
        Text(text, color = Color.White, fontSize = 14.sp)
    }
}

@Composable
fun CustomTextFieldWithLabel(
    label: String,
    name: MutableState<String>,
    placeholder: String,
    modifier: Modifier,
    isError: MutableState<Boolean>,
    error: String,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(label, color = Color.White, fontSize = 16.sp)
        CustomTextField(
            value = name.value,
            onValueChange = { name.value = it },
            placeholder = placeholder,
            modifier = modifier,
            isError = isError.value,
            errorMessage = error
        )
    }
}
