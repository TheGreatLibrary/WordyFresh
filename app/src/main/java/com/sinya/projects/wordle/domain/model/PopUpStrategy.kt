package com.sinya.projects.wordle.domain.model

import com.sinya.projects.wordle.navigation.ScreenRoute

sealed class PopUpStrategy {
    data class ToRoute(val route: ScreenRoute, val inclusive: Boolean = true) : PopUpStrategy()
    data class ToStart(val inclusive: Boolean = false) : PopUpStrategy()
    data object None : PopUpStrategy()
}