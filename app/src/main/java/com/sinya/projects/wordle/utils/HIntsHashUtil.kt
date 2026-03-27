package com.sinya.projects.wordle.utils

import com.sinya.projects.wordle.BuildConfig
import java.security.MessageDigest

object HintsHashUtil {

    private const val SALT = BuildConfig.HINTS_SALT

    fun compute(count: Int, lastRestoredAt: Long): String =
        MessageDigest.getInstance("SHA-256")
            .digest("$count:$lastRestoredAt:$SALT".toByteArray())
            .take(8)
            .joinToString("") { "%02x".format(it) }

    fun verify(count: Int, lastRestoredAt: Long, hash: String): Boolean =
        compute(count, lastRestoredAt) == hash
}