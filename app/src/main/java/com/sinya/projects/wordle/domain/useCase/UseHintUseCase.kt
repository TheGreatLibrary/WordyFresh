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
    suspend operator fun invoke(): UseHintResult {
        val prefs = dataSource.hintsFlow.first()
        val raw = prefs as? HintsRaw.Valid ?: return UseHintResult.NoHints

        if (raw.count <= 0) return UseHintResult.NoHints

        if (raw.usedInRound >= HintsConfig.MAX_HINTS_PER_ROUND) {
            return UseHintResult.RoundLimitReached
        }

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