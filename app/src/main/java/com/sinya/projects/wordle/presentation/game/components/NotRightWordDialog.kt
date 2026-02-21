package com.sinya.projects.wordle.presentation.game.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.ui.features.UiText
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun NotRightWordDialog(
    showNotFoundDialog: Boolean,
    showHardModeHint: UiText?
) {
    AnimatedVisibility(
        visible = showNotFoundDialog || showHardModeHint != null,
        enter = fadeIn(animationSpec = tween(200)) + scaleIn(initialScale = 0.8f),
        exit = fadeOut(animationSpec = tween(150)) + scaleOut(targetScale = 0.9f),
        modifier = Modifier.fillMaxSize() .background(Color.Black.copy(alpha = 0.4f)),
    ) {
        val message = when {
            showNotFoundDialog -> stringResource(R.string.not_found_word)
            showHardModeHint != null -> showHardModeHint.asString()
            else -> ""
        }

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(WordyColor.colors.background)
                    .padding(horizontal = 15.dp, vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    message,
                    color = WordyColor.colors.textPrimary,
                    style = WordyTypography.bodyLarge,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}