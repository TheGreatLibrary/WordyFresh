package com.sinya.projects.wordle.utils

import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.domain.error.DefinitionNotFoundException
import com.sinya.projects.wordle.domain.error.InvalidNicknameException
import com.sinya.projects.wordle.domain.error.NoInternetException
import com.sinya.projects.wordle.domain.error.SessionRestoreException
import com.sinya.projects.wordle.domain.error.UserHasNotProfileException
import com.sinya.projects.wordle.domain.error.UserNotAuthenticatedException
import com.sinya.projects.wordle.domain.error.WordNotFoundException

fun Throwable.getErrorMessage(): Int = when (this) {
    is DefinitionNotFoundException -> R.string.error_definition_not_found
    is InvalidNicknameException -> R.string.error_invalid_nickname
    is NoInternetException -> R.string.error_no_internet
    is UserHasNotProfileException -> R.string.error_no_profile
    is UserNotAuthenticatedException -> R.string.error_not_authenticated
    is WordNotFoundException -> R.string.error_word_not_found
    is SessionRestoreException -> R.string.error_session_restore
    else -> R.string.error_unknown
}

