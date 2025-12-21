package com.sinya.projects.wordle.domain.model

data class AchieveItem(
    val id: Int,
    val categoryName: String,
    val title: String,
    val description: String,
    val condition: String,
    val image: String,
    val count: Int,
    val maxCount: Int,
) {
    val isUnlocked: Boolean get() = count >= maxCount
}