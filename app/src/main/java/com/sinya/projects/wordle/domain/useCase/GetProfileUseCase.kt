package com.sinya.projects.wordle.domain.useCase

import com.sinya.projects.wordle.data.remote.supabase.entity.Profiles
import com.sinya.projects.wordle.domain.repository.ProfileRepository
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class GetProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(): Flow<Result<Profiles?>> {
        return profileRepository.getProfileFlow().flowOn(Dispatchers.IO)
    }
}