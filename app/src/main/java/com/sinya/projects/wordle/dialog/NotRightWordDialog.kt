package com.sinya.projects.wordle.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sinya.projects.wordle.ui.theme.WordleColor
import com.sinya.projects.wordle.ui.theme.WordleTypography

@Composable
fun NotRightWordDialog(
    message: String,
) {
    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = false
        )
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(200)) + scaleIn(initialScale = 0.8f),
            exit = fadeOut(animationSpec = tween(150)) + scaleOut(targetScale = 0.9f)
        ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 20.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color = WordleColor.colors.backgroundCard)
                        .padding(vertical = 13.dp, horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        message,
                        color = WordleColor.colors.textCardPrimary,
                        style = WordleTypography.bodyLarge,
                        fontSize = 20.sp
                    )
            }
        }
    }
}