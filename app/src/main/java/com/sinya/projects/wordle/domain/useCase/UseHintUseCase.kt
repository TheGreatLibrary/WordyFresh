package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.data.local.datastore.HintsDataSource
import com.sinya.projects.wordle.data.local.datastore.HintsRaw
import com.sinya.projects.wordle.domain.model.HintsState
import com.sinya.projects.wordle.domain.model.UseHintResult
import com.sinya.projects.wordle.utils.HintsConfig
import jakarta.inject.Inject
import kotlinx.coroutines.flow.first

class UseHintUseCase @Inject constructor(
    private val dataSource: HintsDataSource
) {
    suspend operator fun invoke(currentState: HintsState): UseHintResult {
        if (currentState.available <= 0) return UseHintResult.NoHints
        if (!currentState.canUseThisRound) return UseHintResult.RoundLimitReached

        val prefs = dataSource.hintsFlow.first()
        val raw = prefs as? HintsRaw.Valid ?: return UseHintResult.NoHints

        val newCount = raw.count - 1
        val newRestoredAt = if (raw.count == HintsConfig.MAX_HINTS) {
            System.currentTimeMillis()
        } else {
            raw.lastRestoredAt
        }

        dataSource.save(newCount, newRestoredAt, raw.usedInRound + 1)
        return UseHintResult.Success(newCount)
    }
}