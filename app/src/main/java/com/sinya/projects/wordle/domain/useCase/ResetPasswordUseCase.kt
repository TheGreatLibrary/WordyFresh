package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.domain.repository.ProfileRepository
import jakarta.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        return profileRepository.resetPassword(email)
    }
}