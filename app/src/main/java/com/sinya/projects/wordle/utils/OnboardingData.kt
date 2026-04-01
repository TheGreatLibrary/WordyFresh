package com.sinya.projects.wordle.utils

import com.sinya.projects.wordle.domain.enums.GameColors
import com.sinya.projects.wordle.domain.model.Cell
import com.sinya.projects.wordle.domain.model.Key

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

    fun getKeyboardExample(): List<List<Key>> = listOf(
        listOf(
            Key('Q', GameColors.GRAY,   null,  GameColors.DEFAULT_KEY),
            Key('W', GameColors.DEFAULT_KEY, null, GameColors.DEFAULT_KEY),
            Key('E', GameColors.GREEN,  'É',   GameColors.YELLOW),
            Key('R', GameColors.YELLOW, 'Ř',   GameColors.DEFAULT_KEY),
            Key('T', GameColors.DEFAULT_KEY, 'Ť', GameColors.DEFAULT_KEY),
            Key('Y', GameColors.GRAY,   'Ý',   GameColors.GRAY),
            Key('U', GameColors.DEFAULT_KEY, 'Ú', GameColors.DEFAULT_KEY),
            Key('I', GameColors.YELLOW, 'Í',   GameColors.DEFAULT_KEY),
            Key('O', GameColors.DEFAULT_KEY, 'Ó', GameColors.DEFAULT_KEY),
            Key('P', GameColors.GRAY,   null,  GameColors.DEFAULT_KEY),
        ),
        listOf(
            Key('A', GameColors.GREEN,  'Á',   GameColors.GREEN),
            Key('S', GameColors.DEFAULT_KEY, 'Š', GameColors.DEFAULT_KEY),
            Key('D', GameColors.GRAY,   'Ď',   GameColors.DEFAULT_KEY),
            Key('F', GameColors.DEFAULT_KEY, null, GameColors.DEFAULT_KEY),
            Key('G', GameColors.DEFAULT_KEY, null, GameColors.DEFAULT_KEY),
            Key('H', GameColors.YELLOW, null,  GameColors.DEFAULT_KEY),
            Key('J', GameColors.DEFAULT_KEY, null, GameColors.DEFAULT_KEY),
            Key('K', GameColors.GRAY,   null,  GameColors.DEFAULT_KEY),
            Key('L', GameColors.DEFAULT_KEY, null, GameColors.DEFAULT_KEY),
            Key('<', GameColors.DEFAULT_KEY, null, GameColors.DEFAULT_KEY),
        ),
        listOf(
            Key('Z', GameColors.DEFAULT_KEY, 'Ž', GameColors.YELLOW),
            Key('X', GameColors.DEFAULT_KEY, null, GameColors.DEFAULT_KEY),
            Key('C', GameColors.GRAY,   'Č',   GameColors.DEFAULT_KEY),
            Key('V', GameColors.DEFAULT_KEY, null, GameColors.DEFAULT_KEY),
            Key('B', GameColors.DEFAULT_KEY, null, GameColors.DEFAULT_KEY),
            Key('N', GameColors.GREEN,  'Ň',   GameColors.DEFAULT_KEY),
            Key('M', GameColors.DEFAULT_KEY, null, GameColors.DEFAULT_KEY),
            Key('Ě', GameColors.YELLOW, null,  GameColors.DEFAULT_KEY),
            Key('Ů', GameColors.DEFAULT_KEY, null, GameColors.DEFAULT_KEY),
            Key('>', GameColors.DEFAULT_KEY, null, GameColors.DEFAULT_KEY),
        )
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