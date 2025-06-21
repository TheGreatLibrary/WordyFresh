package com.sinya.projects.wordle.domain.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "sync_achievements",
    primaryKeys = ["user_id", "achieve_id"],
    foreignKeys = [
        ForeignKey(
            entity = Profiles::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Achievements::class,
            parentColumns = ["id"],
            childColumns = ["achieve_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SyncAchievements(
    @ColumnInfo(name = "achieve_id") val achieveId: Int,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "count") val count: Int = 0,
    @ColumnInfo(name = "updated_at") val updatedAt: String
)