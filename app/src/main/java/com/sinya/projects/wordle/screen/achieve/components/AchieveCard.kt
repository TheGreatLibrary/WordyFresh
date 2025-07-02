package com.sinya.projects.wordle.screen.achieve.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.sinya.projects.wordle.domain.model.data.AchieveItem
import com.sinya.projects.wordle.ui.features.CustomCard
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography
import kotlinx.coroutines.launch

@Composable
fun AchieveCard(
    achieveItem: AchieveItem,
    modifier: Modifier
) {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }

    CustomCard(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        scope.launch { scale.animateTo(0.95f) }
                        tryAwaitRelease()
                        scope.launch {
                            scale.animateTo(
                                1f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        }
                    },
                    onTap = { showDialog = true }
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Image(
                painter = painterResource(getDrawableId(achieveItem.image)),
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        color = if (achieveItem.count < achieveItem.maxCount) WordyColor.colors.foregroundAchievePlaceholder else WordyColor.colors.borderAchieve,
                        shape = CircleShape
                    )
                    .fillMaxWidth()
                    .background(WordyColor.colors.backgroundAchieve)
                    .padding(5.dp)
                    .aspectRatio(1f),
                colorFilter = if (achieveItem.count < achieveItem.maxCount) {
                    ColorFilter.tint(WordyColor.colors.foregroundAchievePlaceholder)
                }
                else null
            )
            Text(
                text = "${achieveItem.count}/${achieveItem.maxCount}",
                style = WordyTypography.bodyMedium,
                color = WordyColor.colors.textCardPrimary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = getLocalizedString(achieveItem.title),
                style = WordyTypography.bodyMedium,
                color = WordyColor.colors.textCardPrimary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }

    if (showDialog) {
        AchieveDialog(
            achieveItem = achieveItem,
            onEvent = { showDialog = false }
        )
    }
}



