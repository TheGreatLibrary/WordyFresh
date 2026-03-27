package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.domain.error.DefinitionNotFoundException
import com.sinya.projects.wordle.domain.error.NoInternetException
import com.sinya.projects.wordle.domain.source.DefinitionDataSource
import com.sinya.projects.wordle.domain.source.WikipediaDataSource
import com.sinya.projects.wordle.domain.source.WiktionaryDataSource
import jakarta.inject.Inject

class GetDefinitionWordUseCase @Inject constructor(
    private val wikipediaDataSource: WikipediaDataSource,
    private val wiktionaryDataSource: WiktionaryDataSource,
) {
    private val sources: List<DefinitionDataSource> = listOf(
        wikipediaDataSource,
        wiktionaryDataSource
    )

    suspend operator fun invoke(word: String, lang: String): Result<String> {
        var lastError: Throwable = DefinitionNotFoundException()

        for (source in sources) {
            val result = source.getDefinition(word, lang)
            result.fold(
                onSuccess = { return Result.success(it) },
                onFailure = { error ->
                    when (error) {
                        is NoInternetException -> return Result.failure(error)
                        else -> lastError = error
                    }
                }
            )
        }

        return Result.failure(lastError)
    }
}