package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.domain.repository.ProfileRepository
import jakarta.inject.Inject

class ImportSessionUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(deepLinkUri: String): Result<Unit> {
        return repository.importSession(deepLinkUri)
    }
}
