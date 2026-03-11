package com.sinya.projects.wordle.ui.features

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.ui.theme.Montserrat
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyShapes
import com.sinya.projects.wordle.ui.theme.gray400
import com.sinya.projects.wordle.ui.theme.green400

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
    errorMessage: String? = null,
    color: Color = Color.Transparent
) {
    val customSelectionColors = TextSelectionColors(
        backgroundColor = green400,
        handleColor = green400
    )

    CompositionLocalProvider(LocalTextSelectionColors provides customSelectionColors) {
        Column {
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
                    color = if (isError) WordyColor.colors.secondary else WordyColor.colors.primary
                ),
                cursorBrush = SolidColor(WordyColor.colors.primary),
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = if (isError) WordyColor.colors.secondary else color,
                        shape = WordyShapes.extraLarge
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
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = errorMessage,
                        color = WordyColor.colors.secondary,
                        fontSize = 12.sp,
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun CustomTextFieldWithLabel(
    label: String,
    name: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier,
    isError: Boolean,
    error: String,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(label, color = WordyColor.colors.textPrimary, fontSize = 16.sp)
        CustomTextField(
            value = name,
            onValueChange = onValueChange,
            placeholder = placeholder,
            modifier = modifier,
            isError = isError,
            errorMessage = error,
            color = WordyColor.colors.primary
        )
    }
}
