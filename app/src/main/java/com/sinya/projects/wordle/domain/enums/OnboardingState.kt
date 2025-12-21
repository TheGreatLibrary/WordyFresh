package com.sinya.projects.wordle.domain.enums

enum class OnboardingState {
    WELCOME,
    CELL_COLORS,
    ATTEMPTS,
    RULES,
    FINISH;

    companion object {
        fun getFirstPlay(): List<OnboardingState> = OnboardingState.entries

        fun getNotFirstPlay(): List<OnboardingState> = OnboardingState.entries.filter { it != WELCOME }
    }
}