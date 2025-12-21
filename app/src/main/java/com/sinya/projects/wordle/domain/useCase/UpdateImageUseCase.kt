package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.domain.repository.ProfileRepository
import jakarta.inject.Inject

class UpdateImageUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(uriPath: String): Result<Unit> {
        return repository.updateImage(uriPath.trim())
    }
}
