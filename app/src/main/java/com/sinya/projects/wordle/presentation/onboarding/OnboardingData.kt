package com.sinya.projects.wordle.presentation.onboarding

import com.sinya.projects.wordle.domain.model.Cell
import com.sinya.projects.wordle.ui.theme.gray600
import com.sinya.projects.wordle.ui.theme.green800
import com.sinya.projects.wordle.ui.theme.yellow

object OnboardingData {
    fun getCellColorsExample(): List<Cell> = listOf(
        Cell(letter = "Б", backgroundColor = green800.value),
        Cell(letter = "О", backgroundColor = yellow.value),
        Cell(letter = "Б", backgroundColor = green800.value),
        Cell(letter = "Е", backgroundColor = yellow.value),
        Cell(letter = "Р", backgroundColor = gray600.value)
    )

    fun getAttemptsExample(): List<Cell> = listOf(
        Cell(letter = "С", backgroundColor = gray600.value),
        Cell(letter = "Е", backgroundColor = green800.value),
        Cell(letter = "Ч", backgroundColor = gray600.value),
        Cell(letter = "К", backgroundColor = gray600.value),
        Cell(letter = "А", backgroundColor = gray600.value),
        Cell(letter = "М", backgroundColor = gray600.value),
        Cell(letter = "Е", backgroundColor = green800.value),
        Cell(letter = "Р", backgroundColor = gray600.value),
        Cell(letter = "И", backgroundColor = gray600.value),
        Cell(letter = "Н", backgroundColor = green800.value),
        Cell(letter = "Б", backgroundColor = green800.value),
        Cell(letter = "О", backgroundColor = gray600.value),
        Cell(letter = "Б", backgroundColor = gray600.value),
        Cell(letter = "Е", backgroundColor = yellow.value),
        Cell(letter = "Р", backgroundColor = gray600.value),
        Cell(letter = "Б"),
        Cell(letter = "О"),
        Cell(letter = "Ч"),
        Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell()
    )

    fun getRulesExample1(): List<Cell> = listOf(
        Cell(letter = "Б", backgroundColor = yellow.value),
        Cell(letter = "Р", backgroundColor = yellow.value),
        Cell(letter = "Ю", backgroundColor = yellow.value),
        Cell(letter = "К", backgroundColor = yellow.value),
        Cell(letter = "И", backgroundColor = green800.value)
    )

    fun getRulesExample2(): List<Cell> = listOf(
        Cell(letter = "Б", backgroundColor = gray600.value),
        Cell(letter = "О", backgroundColor = gray600.value),
        Cell(letter = "Б", backgroundColor = gray600.value),
        Cell(letter = "Е", backgroundColor = green800.value),
        Cell(letter = "Р", backgroundColor = gray600.value)
    )

    fun getFinishExample1(): List<Cell> = listOf(
        Cell(letter = "С", backgroundColor = green800.value),
        Cell(letter = "А", backgroundColor = green800.value),
        Cell(letter = "Л", backgroundColor = green800.value),
        Cell(letter = "А", backgroundColor = green800.value),
        Cell(letter = "Т", backgroundColor = green800.value)
    )

    fun getFinishExample2(): List<Cell> = listOf(
        Cell(letter = "Б", backgroundColor = gray600.value),
        Cell(letter = "О", backgroundColor = gray600.value),
        Cell(letter = "Ч", backgroundColor = gray600.value),
        Cell(letter = "К", backgroundColor = gray600.value),
        Cell(letter = "А", backgroundColor = green800.value)
    )
}