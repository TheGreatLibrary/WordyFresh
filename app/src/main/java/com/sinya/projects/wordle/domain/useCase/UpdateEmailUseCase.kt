package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.domain.repository.ProfileRepository
import jakarta.inject.Inject

class UpdateEmailUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        return profileRepository.updateEmail(email)
    }
}