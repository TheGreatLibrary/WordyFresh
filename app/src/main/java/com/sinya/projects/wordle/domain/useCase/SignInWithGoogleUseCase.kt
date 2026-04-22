package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.domain.repository.ProfileRepository
import io.github.jan.supabase.auth.user.UserInfo
import jakarta.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(tokenId: String): Result<UserInfo?> {
        return profileRepository.signInWithGoogle(tokenId)
    }
}