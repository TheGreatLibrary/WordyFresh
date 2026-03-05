package com.sinya.projects.wordle.domain.repository

import android.net.Uri
import com.sinya.projects.wordle.data.remote.web.LegalLinks.getAvatarFileName
import com.sinya.projects.wordle.domain.source.AvatarLocalDataSource
import com.sinya.projects.wordle.domain.source.AvatarRemoteDataSource
import com.sinya.projects.wordle.domain.source.ImageCompressor
import jakarta.inject.Inject

interface AvatarRepository {
    suspend fun uploadAvatar(userId: String, uri: Uri): Result<Uri>
    suspend fun downloadAvatar(userId: String): Result<Uri?>
    fun deleteLocalAvatar(userId: String)
}

class AvatarRepositoryImpl @Inject constructor(
    private val remoteDataSource: AvatarRemoteDataSource,
    private val localDataSource: AvatarLocalDataSource,
    private val imageCompressor: ImageCompressor
) : AvatarRepository {

    override suspend fun uploadAvatar(userId: String, uri: Uri): Result<Uri> = runCatching {
        val fileName = getAvatarFileName(userId)
        val compressedFile = imageCompressor.compressToSquareWebP(uri, userId)

        remoteDataSource.uploadAvatar(fileName, compressedFile)

        val localUri = localDataSource.saveAvatar(userId, compressedFile.readBytes())
        compressedFile.delete()

        localUri
    }

    override suspend fun downloadAvatar(userId: String): Result<Uri?> = runCatching {
        localDataSource.getLocalAvatar(userId)?.let {
            return@runCatching it
        }

        val fileName = getAvatarFileName(userId)
        val data = remoteDataSource.downloadAvatar(fileName) ?: return@runCatching null

        val localUri = localDataSource.saveAvatar(userId, data)
        localUri
    }

    override fun deleteLocalAvatar(userId: String) {
        localDataSource.deleteAvatar(userId)
    }
}