package com.sinya.projects.wordle.domain.useCase

import android.net.Uri
import com.sinya.projects.wordle.domain.repository.AvatarRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.StateFlow

class GetAvatarUseCase @Inject constructor(
    private val repository: AvatarRepository
) {
    suspend operator fun invoke(userId: String): Result<Uri?> {
        return repository.downloadAvatar(userId)
    }

    fun observeAvatar(): StateFlow<Uri?> = repository.avatarUriFlow
}