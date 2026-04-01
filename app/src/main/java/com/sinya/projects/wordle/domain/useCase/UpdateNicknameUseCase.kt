package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.domain.repository.ProfileRepository
import jakarta.inject.Inject

class UpdateNicknameUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(nickname: String): Result<Unit> {
        return repository.updateNickname(nickname.trim())
    }
}
