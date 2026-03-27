package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.data.local.datastore.HintsDataSource
import com.sinya.projects.wordle.data.local.datastore.HintsRaw
import com.sinya.projects.wordle.domain.model.HintsState
import com.sinya.projects.wordle.utils.HintsConfig
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Duration.Companion.milliseconds

class GetHintsStateUseCase @Inject constructor(
    private val dataSource: HintsDataSource
) {
    operator fun invoke(): Flow<HintsState> = dataSource.hintsFlow.map { raw ->
        when (raw) {
            null -> initDefault()
            is HintsRaw.Tampered  -> initDefault()
            is HintsRaw.Valid     -> computeState(raw)
        }
    }

    private suspend fun initDefault(): HintsState {
        val now = System.currentTimeMillis()
        dataSource.save(HintsConfig.MAX_HINTS, now, 0)
        return HintsState(
            available = HintsConfig.MAX_HINTS,
            usedThisRound = 0,
            nextRestoreIn = null
        )
    }

    private suspend fun computeState(raw: HintsRaw.Valid): HintsState {
        val now = System.currentTimeMillis()
        val elapsed = (now - raw.lastRestoredAt).milliseconds
        val restored = (elapsed / HintsConfig.RESTORE_INTERVAL).toInt()

        return if (restored > 0 && raw.count < HintsConfig.MAX_HINTS) {
            val newCount = (raw.count + restored).coerceAtMost(HintsConfig.MAX_HINTS)
            // сдвигаем кратно интервалу, не теряем остаток
            val newRestoredAt = raw.lastRestoredAt +
                    HintsConfig.RESTORE_INTERVAL.inWholeMilliseconds * restored

            dataSource.save(newCount, newRestoredAt, raw.usedInRound)

            val timeToNext = if (newCount < HintsConfig.MAX_HINTS)
                HintsConfig.RESTORE_INTERVAL - (now - newRestoredAt).milliseconds
            else null

            HintsState(newCount, usedThisRound = raw.usedInRound, nextRestoreIn = timeToNext)
        } else {
            val timeToNext = if (raw.count < HintsConfig.MAX_HINTS)
                HintsConfig.RESTORE_INTERVAL - elapsed
            else null

            HintsState(raw.count, usedThisRound = raw.usedInRound, nextRestoreIn = timeToNext)
        }
    }
}