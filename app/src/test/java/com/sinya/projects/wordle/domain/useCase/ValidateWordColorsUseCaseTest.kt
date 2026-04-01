package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.domain.enums.GameColors
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ValidateWordColorsUseCaseTest {

    private lateinit var useCase: ValidateWordColorsUseCase

    @Before
    fun setUp() {
        useCase = ValidateWordColorsUseCase()
    }

    @Test
    fun `all letters correct - all green`() {
        val result = useCase("КОШКА", "КОШКА")
        assertEquals(List(5) { GameColors.GREEN }, result)
    }

    @Test
    fun `no matching letters - all gray`() {
        val result = useCase("БАНАН", "ВОРОН")
        assertEquals(
            listOf(GameColors.GRAY, GameColors.GRAY, GameColors.GRAY, GameColors.GRAY, GameColors.GREEN),
            result
        )
    }

    @Test
    fun `letter in wrong position - yellow`() {
        val result = useCase("АБВГД", "ДГВБА")
        assertEquals(
            listOf(GameColors.YELLOW, GameColors.YELLOW, GameColors.GREEN, GameColors.YELLOW, GameColors.YELLOW),
            result
        )
    }

    @Test
    fun `duplicate letter in entered word - only one yellow when hidden has one`() {
        val result = useCase("ООБВГ", "ДАВОП")
        assertEquals(GameColors.YELLOW, result[0])
        assertEquals(GameColors.GRAY, result[1])
    }

    @Test
    fun `green takes priority over yellow for same letter`() {
        val result = useCase("ООШКА", "КОШКА")
        assertEquals(GameColors.GRAY, result[0])
        assertEquals(GameColors.GREEN, result[1])
    }
}