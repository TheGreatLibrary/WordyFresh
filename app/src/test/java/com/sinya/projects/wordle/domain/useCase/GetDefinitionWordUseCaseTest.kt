package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.domain.error.DefinitionNotFoundException
import com.sinya.projects.wordle.domain.error.NoInternetException
import com.sinya.projects.wordle.domain.source.WikipediaDataSource
import com.sinya.projects.wordle.domain.source.WiktionaryDataSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetDefinitionWordUseCaseTest {

    private lateinit var wikipedia: WikipediaDataSource
    private lateinit var wiktionary: WiktionaryDataSource
    private lateinit var useCase: GetDefinitionWordUseCase

    @Before
    fun setUp() {
        wikipedia = mockk()
        wiktionary = mockk()
        useCase = GetDefinitionWordUseCase(wikipedia, wiktionary)
    }

    @Test
    fun `wikipedia returns success - returns immediately, wiktionary not called`() = runTest {
        coEvery { wikipedia.getDefinition("кошка", "ru") } returns Result.success("Домашнее животное")

        val result = useCase("кошка", "ru")

        assertTrue(result.isSuccess)
        assertEquals("Домашнее животное", result.getOrNull())
        coVerify(exactly = 0) { wiktionary.getDefinition(any(), any()) }
    }

    @Test
    fun `wikipedia fails - falls back to wiktionary`() = runTest {
        coEvery { wikipedia.getDefinition("кошка", "ru") } returns Result.failure(DefinitionNotFoundException())
        coEvery { wiktionary.getDefinition("кошка", "ru") } returns Result.success("Кошка — млекопитающее")

        val result = useCase("кошка", "ru")

        assertTrue(result.isSuccess)
        assertEquals("Кошка — млекопитающее", result.getOrNull())
    }

    @Test
    fun `both sources fail - returns last error`() = runTest {
        coEvery { wikipedia.getDefinition("кошка", "ru") } returns Result.failure(DefinitionNotFoundException())
        coEvery { wiktionary.getDefinition("кошка", "ru") } returns Result.failure(DefinitionNotFoundException())

        val result = useCase("кошка", "ru")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is DefinitionNotFoundException)
    }

    @Test
    fun `NoInternetException - returns immediately without trying wiktionary`() = runTest {
        coEvery { wikipedia.getDefinition("кошка", "ru") } returns Result.failure(NoInternetException())

        val result = useCase("кошка", "ru")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NoInternetException)
        coVerify(exactly = 0) { wiktionary.getDefinition(any(), any()) }
    }
}