package com.sinya.projects.wordle.presentation.home.friendSheet

import androidx.annotation.StringRes
import com.sinya.projects.wordle.R

data class FriendUiState(
    @StringRes val errorMessage: Int = R.string.is_word_in_database_error,
    val selectedTab: Int = 0,
    val hiddenPlace: String = "",
    val guessedPlace: String = "",
    val isError: Boolean = false
)