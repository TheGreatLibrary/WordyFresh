package com.sinya.projects.wordle.domain.source

import android.content.Context
import android.net.Uri
import com.sinya.projects.wordle.data.remote.web.LegalLinks.getAvatarFileName
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.io.File

interface AvatarLocalDataSource {
    fun saveAvatar(userId: String, data: ByteArray): Uri
    fun getLocalAvatar(userId: String): Uri?
    fun deleteAvatar(userId: String)
}

@Singleton
class FileAvatarLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) : AvatarLocalDataSource {

    private fun getAvatarFile(userId: String) =
        File(context.filesDir, getAvatarFileName(userId))

    override fun saveAvatar(userId: String, data: ByteArray): Uri {
        val file = getAvatarFile(userId)
        file.writeBytes(data)
        return Uri.fromFile(file)
    }

    override fun getLocalAvatar(userId: String): Uri? {
        val file = getAvatarFile(userId)
        return if (file.exists()) Uri.fromFile(file) else null
    }

    override fun deleteAvatar(userId: String) {
        getAvatarFile(userId).delete()
    }
}
