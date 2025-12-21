package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.data.remote.supabase.entity.Profiles
import com.sinya.projects.wordle.domain.repository.ProfileRepository
import jakarta.inject.Inject

class InsertProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(profile: Profiles): Result<Unit> {
        return profileRepository.insertProfile(profile)
    }
}