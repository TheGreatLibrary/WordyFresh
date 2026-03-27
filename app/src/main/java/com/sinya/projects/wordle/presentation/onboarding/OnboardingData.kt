package com.sinya.projects.wordle.presentation.onboarding

import com.sinya.projects.wordle.domain.enums.GameColors
import com.sinya.projects.wordle.domain.model.Cell

object OnboardingData {
    fun getCellColorsExample(): List<Cell> = listOf(
        Cell(letter = "Б", backgroundColor = GameColors.GREEN),
        Cell(letter = "О", backgroundColor = GameColors.YELLOW),
        Cell(letter = "Б", backgroundColor = GameColors.GREEN),
        Cell(letter = "Е", backgroundColor = GameColors.YELLOW),
        Cell(letter = "Р", backgroundColor = GameColors.GRAY)
    )

    fun getAttemptsExample(): List<Cell> = listOf(
        Cell(letter = "С", backgroundColor = GameColors.GRAY),
        Cell(letter = "Е", backgroundColor = GameColors.GREEN),
        Cell(letter = "Ч", backgroundColor = GameColors.GRAY),
        Cell(letter = "К", backgroundColor = GameColors.GRAY),
        Cell(letter = "А", backgroundColor = GameColors.GRAY),
        Cell(letter = "М", backgroundColor = GameColors.GRAY),
        Cell(letter = "Е", backgroundColor = GameColors.GREEN),
        Cell(letter = "Р", backgroundColor = GameColors.GRAY),
        Cell(letter = "И", backgroundColor = GameColors.GRAY),
        Cell(letter = "Н", backgroundColor = GameColors.GREEN),
        Cell(letter = "Б", backgroundColor = GameColors.GREEN),
        Cell(letter = "О", backgroundColor = GameColors.GRAY),
        Cell(letter = "Б", backgroundColor = GameColors.GRAY),
        Cell(letter = "Е", backgroundColor = GameColors.YELLOW),
        Cell(letter = "Р", backgroundColor = GameColors.GRAY),
        Cell(letter = "Б"),
        Cell(letter = "О"),
        Cell(letter = "Ч"),
        Cell(), Cell(hint = "Н"), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell()
    )

    fun getRulesExample1(): List<Cell> = listOf(
        Cell(letter = "Б", backgroundColor = GameColors.YELLOW),
        Cell(letter = "Р", backgroundColor = GameColors.YELLOW),
        Cell(letter = "Ю", backgroundColor = GameColors.YELLOW),
        Cell(letter = "К", backgroundColor = GameColors.YELLOW),
        Cell(letter = "И", backgroundColor = GameColors.GREEN)
    )

    fun getRulesExample2(): List<Cell> = listOf(
        Cell(letter = "Б", backgroundColor = GameColors.GRAY),
        Cell(letter = "О", backgroundColor = GameColors.GRAY),
        Cell(letter = "Б", backgroundColor = GameColors.GRAY),
        Cell(letter = "Е", backgroundColor = GameColors.GREEN),
        Cell(letter = "Р", backgroundColor = GameColors.GRAY)
    )

    fun getMagicExamples(): List<Cell> = listOf(
        Cell(letter = "С", backgroundColor = GameColors.GRAY),
        Cell(letter = "Е", backgroundColor = GameColors.GREEN),
        Cell(letter = "Ч", backgroundColor = GameColors.GRAY),
        Cell(letter = "К", backgroundColor = GameColors.GRAY),
        Cell(letter = "А", backgroundColor = GameColors.GRAY),
        Cell(letter = "М", backgroundColor = GameColors.GRAY),
        Cell(letter = "Е", backgroundColor = GameColors.GREEN),
        Cell(letter = "Р", backgroundColor = GameColors.GRAY),
        Cell(letter = "И", backgroundColor = GameColors.GRAY),
        Cell(letter = "Н", backgroundColor = GameColors.GREEN),
        Cell(letter = "Б", backgroundColor = GameColors.GREEN),
        Cell(letter = "О", backgroundColor = GameColors.GRAY),
        Cell(letter = "Б", backgroundColor = GameColors.GRAY),
        Cell(letter = "Е", backgroundColor = GameColors.YELLOW),
        Cell(letter = "Р", backgroundColor = GameColors.GRAY),
        Cell(letter = "Б", backgroundColor = GameColors.GREEN),
        Cell(letter = "О", backgroundColor = GameColors.YELLOW),
        Cell(letter = "Ч", backgroundColor = GameColors.GRAY),
        Cell(letter = "К", backgroundColor = GameColors.YELLOW),
        Cell(letter = "А", backgroundColor = GameColors.GRAY),
        Cell(letter = "Б"),
        Cell(letter = "Е"),
        Cell(letter = "К"),
        Cell(letter = "О", backgroundColor = GameColors.GREEN),
        Cell(hint = "Н"), Cell(), Cell(), Cell(), Cell(), Cell()
    )

    fun getFinishExample1(): List<Cell> = listOf(
        Cell(letter = "С", backgroundColor = GameColors.GREEN),
        Cell(letter = "А", backgroundColor = GameColors.GREEN),
        Cell(letter = "Л", backgroundColor = GameColors.GREEN),
        Cell(letter = "А", backgroundColor = GameColors.GREEN),
        Cell(letter = "Т", backgroundColor = GameColors.GREEN)
    )

    fun getFinishExample2(): List<Cell> = listOf(
        Cell(letter = "Б", backgroundColor = GameColors.GRAY),
        Cell(letter = "О", backgroundColor = GameColors.GRAY),
        Cell(letter = "Ч", backgroundColor = GameColors.GRAY),
        Cell(letter = "К", backgroundColor = GameColors.GRAY),
        Cell(letter = "А", backgroundColor = GameColors.GREEN)
    )
}