package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.data.local.datastore.HintsDataSource
import com.sinya.projects.wordle.data.local.datastore.HintsRaw
import com.sinya.projects.wordle.domain.model.HintsState
import com.sinya.projects.wordle.domain.model.UseHintResult
import com.sinya.projects.wordle.utils.HintsConfig
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UseHintUseCaseTest {

    private lateinit var dataSource: HintsDataSource
    private lateinit var useCase: UseHintUseCase

    @Before
    fun setUp() {
        dataSource = mockk(relaxed = true)
        useCase = UseHintUseCase(dataSource)
    }

    @Test
    fun `no hints available - returns NoHints`() = runTest {
        val state = HintsState(available = 0, usedThisRound = 0, nextRestoreIn = null)

        val result = useCase(state)

        assertEquals(UseHintResult.NoHints, result)
        coVerify(exactly = 0) { dataSource.save(any(), any(), any()) }
    }

    @Test
    fun `round limit reached - returns RoundLimitReached`() = runTest {
        val state = HintsState(
            available = 3,
            usedThisRound = HintsConfig.MAX_HINTS_PER_ROUND,
            nextRestoreIn = null
        )

        val result = useCase(state)

        assertEquals(UseHintResult.RoundLimitReached, result)
        coVerify(exactly = 0) { dataSource.save(any(), any(), any()) }
    }

    @Test
    fun `hint used successfully - count decremented`() = runTest {
        val state = HintsState(available = 3, usedThisRound = 0, nextRestoreIn = null)
        val raw = HintsRaw.Valid(count = 3, lastRestoredAt = 1000L, usedInRound = 0)

        coEvery { dataSource.hintsFlow } returns flowOf(raw)

        val result = useCase(state)

        assertEquals(UseHintResult.Success(2), result)
        coVerify(exactly = 1) { dataSource.save(2, any(), 1) }
    }

    @Test
    fun `using hint when count was MAX - timer starts now`() = runTest {
        val state = HintsState(available = HintsConfig.MAX_HINTS, usedThisRound = 0, nextRestoreIn = null)
        val raw = HintsRaw.Valid(
            count = HintsConfig.MAX_HINTS,
            lastRestoredAt = 0L,
            usedInRound = 0
        )

        coEvery { dataSource.hintsFlow } returns flowOf(raw)

        val result = useCase(state)

        assertEquals(UseHintResult.Success(HintsConfig.MAX_HINTS - 1), result)
        // lastRestoredAt должен обновиться на текущее время, не остаться 0
        coVerify(exactly = 1) { dataSource.save(HintsConfig.MAX_HINTS - 1, neq(0L), 1) }
    }

    @Test
    fun `raw is not Valid - returns NoHints`() = runTest {
        val state = HintsState(available = 3, usedThisRound = 0, nextRestoreIn = null)

        coEvery { dataSource.hintsFlow } returns flowOf(null)

        val result = useCase(state)

        assertEquals(UseHintResult.NoHints, result)
    }
}