package com.sinya.projects.wordle.screen.achieve

import androidx.lifecycle.ViewModel

class AchieveViewModel: ViewModel() {
    
}

//@Dao
//interface AchievementDao {
//    @Query("""
//        SELECT achievements.id, achievements.title, achievements.description,
//            achievements.complete, achievements.count, achievements.max_count,
//            achievements.icon, categories.name AS category
//        FROM achievements.a
//        JOIN categories.c ON a.category_id = c.id
//        ORDER BY categories.id
//    """)
//    fun getAllAchievements(): Flow<List<AchieveWithCategory>>
//}

data class AchieveWithCategory(
    val id: Int,
    val title: String,
    val description: String,
    val complete: Boolean,
    val count: Int,
    val maxCount: Int,
    val icon: String,
    val category: String
)