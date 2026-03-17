package com.sinya.projects.wordle.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "achievement_translations",
    primaryKeys = ["achieve_id", "lang"],
    foreignKeys = [
        ForeignKey(
            entity = Achievements::class,
            parentColumns = ["id"],
            childColumns = ["achieve_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["achieve_id"])
    ]
)
data class AchievementTranslations(
    @ColumnInfo(name = "achieve_id") val categoryId: Int,
    @ColumnInfo(name = "lang") val lang: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "condition") val condition: String
)