package com.sinya.projects.wordle.domain.useCase

import app.cash.turbine.test
import com.sinya.projects.wordle.data.local.datastore.HintsDataSource
import com.sinya.projects.wordle.data.local.datastore.HintsRaw
import com.sinya.projects.wordle.utils.HintsConfig
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class GetHintsStateUseCaseTest {

    private lateinit var dataSource: HintsDataSource
    private lateinit var useCase: GetHintsStateUseCase

    @Before
    fun setUp() {
        dataSource = mockk(relaxed = true)
        useCase = GetHintsStateUseCase(dataSource)
    }

    @Test
    fun `null raw - emits default state and saves defaults`() = runTest {
        every { dataSource.hintsFlow } returns flowOf(null)

        useCase().test {
            val state = awaitItem()
            assertEquals(HintsConfig.MAX_HINTS, state.available)
            assertEquals(0, state.usedThisRound)
            assertNull(state.nextRestoreIn)
            awaitComplete()
        }

        coVerify(exactly = 1) { dataSource.save(HintsConfig.MAX_HINTS, any(), 0) }
    }

    @Test
    fun `tampered raw - emits default state`() = runTest {
        every { dataSource.hintsFlow } returns flowOf(HintsRaw.Tampered)

        useCase().test {
            val state = awaitItem()
            assertEquals(HintsConfig.MAX_HINTS, state.available)
            awaitComplete()
        }
    }

    @Test
    fun `valid raw with full hints - no timer`() = runTest {
        val raw = HintsRaw.Valid(
            count = HintsConfig.MAX_HINTS,
            lastRestoredAt = System.currentTimeMillis(),
            usedInRound = 0
        )
        every { dataSource.hintsFlow } returns flowOf(raw)

        useCase().test {
            val state = awaitItem()
            assertEquals(HintsConfig.MAX_HINTS, state.available)
            assertNull(state.nextRestoreIn) // полные хинты — таймер не нужен
            awaitComplete()
        }
    }

    @Test
    fun `valid raw with less than max hints - timer present`() = runTest {
        val raw = HintsRaw.Valid(
            count = HintsConfig.MAX_HINTS - 1,
            lastRestoredAt = System.currentTimeMillis(),
            usedInRound = 0
        )
        every { dataSource.hintsFlow } returns flowOf(raw)

        useCase().test {
            val state = awaitItem()
            assertEquals(HintsConfig.MAX_HINTS - 1, state.available)
            assertNotNull(state.nextRestoreIn) // не полные — таймер должен быть
            awaitComplete()
        }
    }

    @Test
    fun `valid raw with elapsed time - hints restored`() = runTest {
        val twoIntervalsAgo = System.currentTimeMillis() -
                HintsConfig.RESTORE_INTERVAL.inWholeMilliseconds * 2
        val raw = HintsRaw.Valid(
            count = HintsConfig.MAX_HINTS - 3,
            lastRestoredAt = twoIntervalsAgo,
            usedInRound = 0
        )
        every { dataSource.hintsFlow } returns flowOf(raw)

        useCase().test {
            val state = awaitItem()
            assertEquals(HintsConfig.MAX_HINTS - 1, state.available)
            coVerify(exactly = 1) { dataSource.save(HintsConfig.MAX_HINTS - 1, any(), 0) }
            awaitComplete()
        }
    }

    @Test
    fun `restored hints dont exceed max`() = runTest {
        val longAgo = System.currentTimeMillis() -
                HintsConfig.RESTORE_INTERVAL.inWholeMilliseconds * 100
        val raw = HintsRaw.Valid(
            count = 0,
            lastRestoredAt = longAgo,
            usedInRound = 0
        )
        every { dataSource.hintsFlow } returns flowOf(raw)

        useCase().test {
            val state = awaitItem()
            assertEquals(HintsConfig.MAX_HINTS, state.available)
            awaitComplete()
        }
    }
}