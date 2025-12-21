package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.data.remote.supabase.entity.Profiles
import com.sinya.projects.wordle.domain.repository.ProfileRepository
import jakarta.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(): Result<Profiles> {
        return profileRepository.getProfile()
    }
}