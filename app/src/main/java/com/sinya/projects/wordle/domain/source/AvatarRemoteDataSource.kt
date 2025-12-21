package com.sinya.projects.wordle.domain.source

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.NotFoundRestException
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.io.File

interface AvatarRemoteDataSource {
    suspend fun uploadAvatar(fileName: String, file: File)
    suspend fun downloadAvatar(fileName: String): ByteArray?
}

@Singleton
class SupabaseAvatarDataSource @Inject constructor(
    private val supabase: SupabaseClient
) : AvatarRemoteDataSource {

    override suspend fun uploadAvatar(fileName: String, file: File) {
        supabase.storage.from("avatars").upload(fileName, file) { upsert = true }
    }

    override suspend fun downloadAvatar(fileName: String): ByteArray? {
        return try {
            supabase.storage.from("avatars").downloadAuthenticated(fileName)
        } catch (_: NotFoundRestException) {
            null
        }
    }
}