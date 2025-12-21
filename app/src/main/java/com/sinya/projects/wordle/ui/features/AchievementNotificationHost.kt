package com.sinya.projects.wordle.ui.features

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.data.local.achievement.AchievementEvent
import com.sinya.projects.wordle.data.local.achievement.AchievementNotificationViewModel
import com.sinya.projects.wordle.domain.model.AchieveItem
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AchievementNotificationHost(
    viewModel: AchievementNotificationViewModel = hiltViewModel(),
    onAchievementClick: (AchieveItem) -> Unit = {}
) {
    var currentAchievement by remember { mutableStateOf<AchieveItem?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.achievementEventBus.events.collect { event ->
            when (event) {
                is AchievementEvent.Unlocked -> {
                    currentAchievement = event.achievement
                    // Автоскрытие через 4 секунды
                    scope.launch {
                        delay(8000)
                        currentAchievement = null
                    }
                }
                else -> {}
            }
        }
    }

    AnimatedVisibility(
        visible = currentAchievement != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
    ) {
        
        currentAchievement?.let { achievement ->
            AchievementNotificationCard(
                achievement = achievement,
                onDismiss = { currentAchievement = null },
                onClick = {
                    onAchievementClick(achievement)
                    currentAchievement = null
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementNotificationCard(
    achievement: AchieveItem,
    onDismiss: () -> Unit,
    onClick: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it != SwipeToDismissBoxValue.Settled) {
                onDismiss()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {}
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(125.dp)
                .padding(13.dp)
                .clickable(onClick = onClick),
            colors = CardDefaults.cardColors(containerColor = WordyColor.colors.backgroundCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(
                    painter = painterResource(getDrawableId(achievement.image)),
                    contentDescription = getLocalizedString(achievement.title),
                    modifier = Modifier
                        .clip(CircleShape)
                        .fillMaxHeight()
                        .border(
                            width = 2.dp,
                            color = WordyColor.colors.borderAchieve,
                            shape = CircleShape
                        )
                        .background(WordyColor.colors.backgroundAchieve)
                        .padding(5.dp),
                )

                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically)
                ) {
                    Text(
                        text = getLocalizedString(achievement.title),
                        style = WordyTypography.titleMedium,
                        color = WordyColor.colors.textCardPrimary,
                        fontSize = 21.sp,
                    )
                    Text(
                        text = stringResource(R.string.achieve_is_unlocked),
                        style = WordyTypography.bodyMedium,
                        color = WordyColor.colors.textCardSecondary,
                        fontSize = 14.sp,
                    )
                }
            }
        }
    }
}

