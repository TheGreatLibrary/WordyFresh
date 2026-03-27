package com.sinya.projects.wordle.data.local.achievement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.wordle.domain.model.AchieveItem
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AchievementNotificationViewModel @Inject constructor(
    private val achievementEventBus: AchievementEventBus
) : ViewModel() {
    private val _currentAchievement = MutableStateFlow<AchieveItem?>(null)
    val currentAchievement = _currentAchievement.asStateFlow()

    private var dismissJob: Job? = null

    init {
        viewModelScope.launch {
            achievementEventBus.events.collect { event ->
                when (event) {
                    is AchievementEvent.Unlocked -> {
                        dismissJob?.cancel()
                        _currentAchievement.value = event.achievement
                        dismissJob = launch {
                            delay(8000)
                            _currentAchievement.value = null
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    fun dismiss() {
        dismissJob?.cancel()
        _currentAchievement.value = null
    }
}