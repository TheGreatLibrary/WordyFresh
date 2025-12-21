package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.domain.error.InvalidNicknameException
import com.sinya.projects.wordle.domain.repository.ProfileRepository
import jakarta.inject.Inject

class UpdateNicknameUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(nickname: String): Result<Unit> {
        if (nickname.trim().isEmpty()) {
            return Result.failure(InvalidNicknameException())
        }
        return repository.updateNickname(nickname.trim())
    }
}
