package com.sinya.projects.wordle.screen.achieve

data class AchieveItem(
    val id: Int,
    val categoryName: String,
    val title: String,
    val description: String,
    val condition: String,
    val image: String,
    val count: Int,
    val maxCount: Int,
)