package com.sinya.projects.wordle.screen.onboarding

sealed class OnboardingPageUiState {
    data object Welcome : OnboardingPageUiState()
    data object CellColors : OnboardingPageUiState()
    data object Attempts : OnboardingPageUiState()
    data object Rules : OnboardingPageUiState()
    data object Finish : OnboardingPageUiState()
}