package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.data.local.achievement.AchievementCondition
import com.sinya.projects.wordle.data.local.achievement.AchievementEvent
import com.sinya.projects.wordle.data.local.achievement.AchievementEventBus
import com.sinya.projects.wordle.data.local.achievement.AchievementId
import com.sinya.projects.wordle.data.local.achievement.AchievementTrigger
import com.sinya.projects.wordle.data.local.achievement.ConditionFactory
import com.sinya.projects.wordle.domain.enums.GameMode
import com.sinya.projects.wordle.domain.enums.TypeAchievement
import com.sinya.projects.wordle.domain.model.AchieveItem
import com.sinya.projects.wordle.domain.repository.AchievementRepository
import com.sinya.projects.wordle.domain.repository.StatisticRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CheckAchievementUseCaseTest {

    private lateinit var achievementRepository: AchievementRepository
    private lateinit var achievementEventBus: AchievementEventBus
    private lateinit var statisticRepository: StatisticRepository
    private lateinit var useCase: CheckAchievementUseCase

    private val condition = mockk<AchievementCondition>()

    @Before
    fun setUp() {
        achievementRepository = mockk(relaxed = true)
        achievementEventBus = mockk(relaxed = true)
        statisticRepository = mockk(relaxed = true)
        useCase = CheckAchievementUseCase(achievementRepository, achievementEventBus, statisticRepository)

        mockkObject(ConditionFactory)
        mockkObject(AchievementId)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // --- вспомогательная функция для создания тестового achievement ---
    private fun makeAchieve(
        id: Int,
        isUnlocked: Boolean = false,
        count: Int = 0,
        maxCount: Int = 10
    ) = AchieveItem(
        id = id,
        categoryName = "",
        title = "",
        description = "",
        condition = "",
        image = "",
        count = if (isUnlocked) 10 else count,
        hidden = true,
        maxCount = maxCount
    )

    @Test
    fun `non-streak achievement - condition satisfied and unlocked - emits Unlocked`() = runTest {
        val achieve = makeAchieve(AchievementId.FIRST_TRY_WIN.id, isUnlocked = false, count = 0, maxCount = 1)

        val trigger = AchievementTrigger.GameFinishedTrigger(
            isWin = true,
            mode = GameMode.FRIENDLY,
            word = "КОШКА",
            attemptsWords = listOf("КОШКА"),
            lang = "ru",
            length = 5,
            rowAttempts = 1,
            timeSeconds = 100
        )

        every { AchievementId.fromId(AchievementId.FIRST_TRY_WIN.id) } returns AchievementId.FIRST_TRY_WIN
        every { ConditionFactory.create(AchievementId.FIRST_TRY_WIN) } returns condition
        every { ConditionFactory.getTypeAchievement(AchievementId.FIRST_TRY_WIN) } returns TypeAchievement.BASED
        every { condition.isSatisfied(trigger) } returns true
        every { condition.getIncrement(trigger) } returns 1

        coEvery { achievementRepository.getAllAchievements("ru") } returns Result.success(listOf(achieve))

        val result = useCase(trigger, "ru")

        assertTrue(result.isSuccess)
        val events = result.getOrNull()!!
        assertTrue(events.any { it is AchievementEvent.Unlocked })
        coVerify { achievementRepository.unlockIncrement(AchievementId.FIRST_TRY_WIN.id, 1) }
        coVerify { achievementEventBus.emit(any<AchievementEvent.Unlocked>()) }
    }

    @Test
    fun `non-streak achievement - condition satisfied but not yet unlocked - emits ProgressUpdated`() = runTest {
        // count=2, нужно 5 для unlock — прогресс, но не разблокировано
        val achieve = makeAchieve(AchievementId.PLAY_50_GAMES.id, isUnlocked = false, count = 2)
        val trigger = AchievementTrigger.GameFinishedTrigger(
            isWin = true,
            mode = GameMode.FRIENDLY,
            word = "КОШКА",
            attemptsWords = listOf("КОШКА"),
            lang = "ru",
            length = 5,
            rowAttempts = 1,
            timeSeconds = 100
        )

        every { AchievementId.fromId(AchievementId.PLAY_50_GAMES.id) } returns AchievementId.PLAY_50_GAMES
        every { ConditionFactory.create(AchievementId.PLAY_50_GAMES) } returns condition
        every { ConditionFactory.getTypeAchievement(AchievementId.PLAY_50_GAMES) } returns TypeAchievement.STREAK
        every { condition.isSatisfied(trigger) } returns true
        every { condition.getIncrement(trigger) } returns 1

        coEvery { achievementRepository.getAllAchievements("ru") } returns Result.success(listOf(achieve))

        val result = useCase(trigger, "ru")

        assertTrue(result.isSuccess)
        val events = result.getOrNull()!!
        assertTrue(events.any { it is AchievementEvent.ProgressUpdated })
        coVerify { achievementEventBus.emit(any<AchievementEvent.ProgressUpdated>()) }
    }

    @Test
    fun `already unlocked achievement - skipped`() = runTest {
        val achieve = makeAchieve(AchievementId.FIRST_GAME.id, isUnlocked = true, count = 1)
        val trigger = AchievementTrigger.GameFinishedTrigger(
            isWin = true,
            mode = GameMode.FRIENDLY,
            word = "КОШКА",
            attemptsWords = listOf("КОШКА"),
            lang = "ru",
            length = 5,
            rowAttempts = 1,
            timeSeconds = 100
        )

        every { AchievementId.fromId(AchievementId.FIRST_GAME.id) } returns AchievementId.FIRST_GAME
        every { ConditionFactory.create(AchievementId.FIRST_GAME) } returns condition
        every { ConditionFactory.getTypeAchievement(AchievementId.FIRST_GAME) } returns TypeAchievement.BASED
        every { condition.isSatisfied(trigger) } returns true

        coEvery { achievementRepository.getAllAchievements("ru") } returns Result.success(listOf(achieve))

        val result = useCase(trigger, "ru")

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.isEmpty())
        coVerify(exactly = 0) { achievementRepository.unlockIncrement(any(), any()) }
    }

    @Test
    fun `streak achievement - streak reached threshold - emits Unlocked`() = runTest {
        val achieve = makeAchieve(AchievementId.WIN_STREAK_10.id, isUnlocked = false, count = 0)
        val trigger = AchievementTrigger.GameFinishedTrigger(
            isWin = true,
            mode = GameMode.FRIENDLY,
            word = "КОШКА",
            attemptsWords = listOf("КОШКА"),
            lang = "ru",
            length = 5,
            rowAttempts = 1,
            timeSeconds = 100
        )

        every { AchievementId.fromId(AchievementId.WIN_STREAK_10.id) } returns AchievementId.WIN_STREAK_10
        every { ConditionFactory.create(AchievementId.WIN_STREAK_10) } returns condition
        every { ConditionFactory.getTypeAchievement(AchievementId.WIN_STREAK_10) } returns TypeAchievement.STREAK
        every { condition.isSatisfied(trigger) } returns true
        coEvery { statisticRepository.getCurrentStreak(isWin = true, true) } returns 10

        coEvery { achievementRepository.getAllAchievements("ru") } returns Result.success(listOf(achieve))

        val result = useCase(trigger, "ru")

        assertTrue(result.isSuccess)
        coVerify { achievementRepository.setOfflineCount(AchievementId.WIN_STREAK_10.id, 10) }
        coVerify { achievementRepository.resetSyncCount(AchievementId.WIN_STREAK_10.id) }
    }

    @Test
    fun `platinum unlocked when all other achievements unlocked`() = runTest {
        val normalAchieve = makeAchieve(AchievementId.FIRST_GAME.id, isUnlocked = true, count = 1)
        val platinumAchieve = makeAchieve(AchievementId.PLATINUM.id, isUnlocked = false, count = 0)
        val trigger = AchievementTrigger.GameFinishedTrigger(
            isWin = true,
            mode = GameMode.FRIENDLY,
            word = "КОШКА",
            attemptsWords = listOf("КОШКА"),
            lang = "ru",
            length = 5,
            rowAttempts = 1,
            timeSeconds = 100
        )

        every { AchievementId.fromId(AchievementId.FIRST_GAME.id) } returns AchievementId.FIRST_GAME
        every { ConditionFactory.create(AchievementId.FIRST_GAME) } returns condition
        every { ConditionFactory.getTypeAchievement(AchievementId.FIRST_GAME) } returns TypeAchievement.BASED
        every { condition.isSatisfied(trigger) } returns false

        every { AchievementId.fromId(AchievementId.PLATINUM.id) } returns AchievementId.PLATINUM
        every { ConditionFactory.create(AchievementId.PLATINUM) } returns condition
        every { ConditionFactory.getTypeAchievement(AchievementId.PLATINUM) } returns TypeAchievement.BASED

        coEvery { achievementRepository.getAllAchievements("ru") } returnsMany listOf(
            Result.success(listOf(normalAchieve, platinumAchieve)),
            Result.success(listOf(normalAchieve, platinumAchieve))
        )

        val result = useCase(trigger, "ru")

        assertTrue(result.isSuccess)
        coVerify { achievementRepository.unlockIncrement(AchievementId.PLATINUM.id, 1) }
        coVerify { achievementEventBus.emit(any<AchievementEvent.Unlocked>()) }
    }

    @Test
    fun `repository throws exception - returns failure`() = runTest {
        val trigger = AchievementTrigger.GameFinishedTrigger(
            isWin = true,
            mode = GameMode.FRIENDLY,
            word = "КОШКА",
            attemptsWords = listOf("КОШКА"),
            lang = "ru",
            length = 5,
            rowAttempts = 1,
            timeSeconds = 100
        )
        coEvery { achievementRepository.getAllAchievements("ru") } throws RuntimeException("DB error")

        val result = useCase(trigger, "ru")

        assertTrue(result.isFailure)
        assertEquals("DB error", result.exceptionOrNull()?.message)
    }
}