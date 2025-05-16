package com.sinya.projects.wordle.domain.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "sync_achievements",
    foreignKeys = [
        ForeignKey(
            entity = Profiles::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoriesAchieve::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SyncAchievements(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "category_id") val categoryId: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "icon_url") val iconUrl: String,
    @ColumnInfo(name = "count") val count: Int = 0,
    @ColumnInfo(name = "max_count") val maxCount: Int = 1,
    @ColumnInfo(name = "updated_at") val updatedAt: String
)