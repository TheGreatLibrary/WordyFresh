package com.sinya.projects.wordle.presentation.home.friendSheet.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.ui.features.CustomTextField
import com.sinya.projects.wordle.ui.features.RoundedButton
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyShapes
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun TabContent(
    description: String,
    placeholder: String,
    errorMessage: String,
    textButton: String,
    value: String,
    isLoading: Boolean,
    isError: Boolean,
    onValueChange: (String) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onShareClick: (() -> Unit)? = null
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = description,
            color = WordyColor.colors.textPrimary,
            style = WordyTypography.bodyMedium,
            fontSize = 14.sp,
        )

        CustomTextField(
            value = value,
            placeholder = placeholder,
            onValueChange = onValueChange,
            modifier = modifier,
            isError = isError,
            errorMessage = errorMessage,
            color = WordyColor.colors.primary
        )
        Row(
            modifier = Modifier.height(45.dp).padding(vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RoundedButton(
                modifier = Modifier.fillMaxWidth(0.6f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = WordyColor.colors.backgroundActiveBtnMkI,
                    contentColor = WordyColor.colors.textForActiveBtnMkI
                ),
                contentPadding = PaddingValues(vertical = 0.dp, horizontal = 10.dp),
                onClick = onClick,
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = WordyColor.colors.borderAchieve,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = textButton,
                        fontSize = 14.sp,
                        style = WordyTypography.bodyMedium
                    )
                }
            }
            onShareClick?.let {
                Button(
                    modifier = Modifier
                        .aspectRatio(1f),
                    shape = WordyShapes.extraLarge,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WordyColor.colors.backgroundActiveBtnMkII,
                        contentColor = WordyColor.colors.textForActiveBtnMkII
                    ),
                    contentPadding = PaddingValues(0.dp),
                    onClick = onShareClick
                ) {
                    Image(
                        painter = painterResource(R.drawable.friend_url),
                        modifier = Modifier.size(26.dp),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(WordyColor.colors.textForActiveBtnMkII)
                    )
                }
            }
        }
    }
}
