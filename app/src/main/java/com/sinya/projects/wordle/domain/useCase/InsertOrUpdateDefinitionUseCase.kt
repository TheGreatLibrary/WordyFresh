package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.data.local.datastore.SettingsEngine
import com.sinya.projects.wordle.domain.error.DefinitionNotFoundException
import com.sinya.projects.wordle.domain.error.NoInternetException
import com.sinya.projects.wordle.domain.error.WordNotFoundException
import com.sinya.projects.wordle.domain.repository.DictionaryRepository
import com.sinya.projects.wordle.utils.ErrorLocalizer
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InsertOrUpdateDefinitionUseCase @Inject constructor(
    private val getDefinitionWordUseCase: GetDefinitionWordUseCase,
    private val settingsEngine: SettingsEngine,
    private val repository: DictionaryRepository
) {
    suspend operator fun invoke(word: String): Result<String> = withContext(Dispatchers.IO) {
        val wordObj = repository.getWord(word).getOrNull()
            ?: return@withContext Result.failure(WordNotFoundException())

        getDefinitionWordUseCase(wordObj.word, wordObj.language).fold(
            onSuccess = { definition ->
                repository.saveDefinition(wordObj.id, definition)
                Result.success(definition)
            },
            onFailure = { exception ->
                when (exception) {
                    is NoInternetException,
                    is DefinitionNotFoundException -> {
                        val lang = wordObj.language
                        val errorMessage = ErrorLocalizer.getMessage(exception, settingsEngine.uiState.value.language)

                        repository.saveDefinition(wordObj.id, errorMessage)
                        Result.success(errorMessage)
                    }
                    else -> Result.failure(exception)
                }
            }
        )
    }
}