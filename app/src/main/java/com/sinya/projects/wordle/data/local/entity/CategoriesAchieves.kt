package com.sinya.projects.wordle.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories_achieves")
data class CategoriesAchieves(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "title") val title: String,
)
