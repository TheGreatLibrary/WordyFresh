package com.sinya.projects.wordle.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "category_achieve_translations",
    primaryKeys = ["category_id", "lang"],
    foreignKeys = [
        ForeignKey(
            entity = CategoriesAchieves::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["category_id"])
    ]
)
data class CategoryAchieveTranslations(
    @ColumnInfo(name = "category_id") val categoryId: Int,
    @ColumnInfo(name = "lang") val lang: String,
    @ColumnInfo(name = "name") val name: String
)