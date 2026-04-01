package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.domain.enums.GameColors
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CheckHardModeRulesUseCaseTest {

    private lateinit var validateWordColorsUseCase: ValidateWordColorsUseCase
    private lateinit var useCase: CheckHardModeRulesUseCase

    @Before
    fun setUp() {
        validateWordColorsUseCase = mockk()
        useCase = CheckHardModeRulesUseCase(validateWordColorsUseCase)
    }

    @Test
    fun `valid word - returns Valid`() {
        every { validateWordColorsUseCase("КОШКА", "КОШКА") } returns
                List(5) { GameColors.GREEN }

        val result = useCase("КОШКА", "КОШКА", "КОШКА", 5)

        assertEquals(CheckHardModeRulesUseCase.HardModeResult.Valid, result)
    }

    @Test
    fun `green letter not in same position - returns ExactPositionError`() {
        // предыдущее слово дало GREEN на позиции 0 (буква К)
        every { validateWordColorsUseCase("КОШКА", "КАБАН") } returns
                listOf(GameColors.GREEN, GameColors.GRAY, GameColors.GRAY, GameColors.GRAY, GameColors.GRAY)

        // новое слово не содержит К на позиции 0
        val result = useCase("АБВГД", "КОШКА", "КАБАН", 5)

        assertEquals(
            CheckHardModeRulesUseCase.HardModeResult.ExactPositionError('К', 1),
            result
        )
    }

    @Test
    fun `yellow letter missing from new word - returns LetterRequiredError`() {
        // предыдущее слово дало YELLOW на позиции 0 (буква К)
        every { validateWordColorsUseCase("АКВАА", "КАБАН") } returns
                listOf(GameColors.YELLOW, GameColors.GRAY, GameColors.GRAY, GameColors.GRAY, GameColors.GRAY)

        // новое слово не содержит К вообще
        val result = useCase("БББBB", "АКВАА", "КАБАН", 5)

        assertEquals(
            CheckHardModeRulesUseCase.HardModeResult.LetterRequiredError('А'),
            result
        )
    }

    @Test
    fun `green letter present in same position - Valid`() {
        every { validateWordColorsUseCase("КАБАН", "КОШКА") } returns
                listOf(GameColors.GREEN, GameColors.YELLOW, GameColors.GRAY, GameColors.GRAY, GameColors.GRAY)

        val result = useCase("КРЫША", "КАБАН", "КОШКА", 5)

        assertEquals(CheckHardModeRulesUseCase.HardModeResult.Valid, result)
    }

    @Test
    fun `all gray - Valid`() {
        every { validateWordColorsUseCase("БББBB", "КОШКА") } returns
                listOf(GameColors.GRAY, GameColors.GRAY, GameColors.GRAY, GameColors.GRAY, GameColors.GRAY)

        val result = useCase("ААААА", "БББBB", "КОШКА", 5)

        assertEquals(CheckHardModeRulesUseCase.HardModeResult.Valid, result)
    }
}