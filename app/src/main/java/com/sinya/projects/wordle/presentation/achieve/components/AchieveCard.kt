package com.sinya.projects.wordle.presentation.achieve.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.domain.model.AchieveItem
import com.sinya.projects.wordle.ui.features.CustomCard
import com.sinya.projects.wordle.ui.features.getDrawableId
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography
import com.sinya.projects.wordle.utils.obfuscate

@Composable
fun AchieveCard(
    achieveItem: AchieveItem,
    modifier: Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    CustomCard(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { showDialog = true }
                )
            }
    ) {
        AchieveCardContent(achieveItem = achieveItem)
    }

    if (showDialog) {
        AchieveDialog(
            achieveItem = achieveItem,
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
private fun AchieveCardContent(
    achieveItem: AchieveItem,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        AchieveImage(
            achieveItem = achieveItem,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        )

        Text(
            text = "${achieveItem.count}/${achieveItem.maxCount}",
            style = WordyTypography.bodyMedium,
            color = WordyColor.colors.textCardPrimary,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )

        Text(
            text = if (achieveItem.hidden && !achieveItem.isUnlocked) achieveItem.title.obfuscate() else achieveItem.title,
            style = WordyTypography.bodyMedium,
            color = WordyColor.colors.textCardPrimary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AchieveImage(
    achieveItem: AchieveItem,
    modifier: Modifier = Modifier
) {
    val isLocked = achieveItem.count < achieveItem.maxCount
    val borderColor = if (isLocked) {
        WordyColor.colors.foregroundAchievePlaceholder
    } else {
        WordyColor.colors.borderAchieve
    }

    Image(
        painter = painterResource(getDrawableId(achieveItem.image)),
        contentDescription = achieveItem.title,
        modifier = modifier
            .clip(CircleShape)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = CircleShape
            )
            .background(WordyColor.colors.backgroundAchieve)
            .padding(5.dp),
        colorFilter = if (isLocked) {
            ColorFilter.tint(WordyColor.colors.foregroundAchievePlaceholder)
        } else {
            null
        }
    )
}


