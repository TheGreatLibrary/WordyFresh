package com.sinya.projects.wordle.domain.model

import androidx.annotation.DrawableRes

data class RadioItem<T>(
    val text: String?,
    @DrawableRes val icon: Int? = null,
    val value: T? = null
)