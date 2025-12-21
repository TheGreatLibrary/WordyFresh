package com.sinya.projects.wordle.data.local.achievement

import com.sinya.projects.wordle.domain.model.AchieveItem
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed interface AchievementEvent {
    data class Unlocked(val achievement: AchieveItem) : AchievementEvent
    data class ProgressUpdated(val achievement: AchieveItem) : AchievementEvent
}

@Singleton
class AchievementEventBus @Inject constructor() {
    private val _events = MutableSharedFlow<AchievementEvent>(
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events: SharedFlow<AchievementEvent> = _events.asSharedFlow()

    suspend fun emit(event: AchievementEvent) {
        _events.emit(event)
    }
}