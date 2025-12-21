package com.sinya.projects.wordle.domain.useCase

import android.net.Uri
import com.sinya.projects.wordle.domain.repository.AvatarRepository
import jakarta.inject.Inject

class UploadAvatarUseCase @Inject constructor(
    private val repository: AvatarRepository
) {
    suspend operator fun invoke(userId: String, uri: Uri): Result<Uri> {
        return repository.uploadAvatar(userId, uri)
    }
}