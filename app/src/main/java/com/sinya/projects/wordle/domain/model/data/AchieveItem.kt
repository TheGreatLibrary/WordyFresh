package com.sinya.projects.wordle.domain.model.data

data class AchieveItem(
    val id: String,
    val categoryId: String,
    val title: String,
    val description: String,
    val iconUrl: String,
    val count: Int,
    val maxCount: Int,
)