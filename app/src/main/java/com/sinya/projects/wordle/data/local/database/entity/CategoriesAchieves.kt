package com.sinya.projects.wordle.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories_achieves")
data class CategoriesAchieves(
    @PrimaryKey val id: Int
)

