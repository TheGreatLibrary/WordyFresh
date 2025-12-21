package com.sinya.projects.wordle.data.local.achievement

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class AchievementNotificationViewModel @Inject constructor(
    val achievementEventBus: AchievementEventBus
) : ViewModel()