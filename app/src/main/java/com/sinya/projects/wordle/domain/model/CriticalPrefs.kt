package com.sinya.projects.wordle.domain.model

data class CriticalPrefs(
    val dark: Boolean,
    val language: String,
    val onboardingDone: Boolean?
)