package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.domain.error.DefinitionNotFoundException
import com.sinya.projects.wordle.domain.error.NoInternetException
import com.sinya.projects.wordle.domain.source.DictionaryDataSource
import com.sinya.projects.wordle.domain.source.WikipediaDataSource
import com.sinya.projects.wordle.domain.source.WiktionaryDataSource
import jakarta.inject.Inject

class GetDefinitionWordUseCase @Inject constructor(
    private val wikipediaDataSource: WikipediaDataSource,
    private val wiktionaryDataSource: WiktionaryDataSource,
) {
    private val sources: List<DictionaryDataSource> = listOf(
        wiktionaryDataSource,
        wikipediaDataSource
    )

    suspend operator fun invoke(word: String, lang: String): Result<String> {
        var lastError: Throwable = DefinitionNotFoundException()

        for (source in sources) {
            val result = source.getDefinition(word, lang)
            result.fold(
                onSuccess = { return Result.success(it) },
                onFailure = { error ->
                    when (error) {
                        is NoInternetException -> return Result.failure(error)  // сразу выходим
                        else -> lastError = error  // пробуем следующий источник
                    }
                }
            )
        }

        return Result.failure(lastError)
    }
}