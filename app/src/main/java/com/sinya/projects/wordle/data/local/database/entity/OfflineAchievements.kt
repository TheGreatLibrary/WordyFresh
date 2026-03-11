package com.sinya.projects.wordle.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "offline_achievements",
    foreignKeys = [
        ForeignKey(
            entity = Achievements::class,
            parentColumns = ["id"],
            childColumns = ["achieve_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class OfflineAchievements(
    @PrimaryKey @ColumnInfo(name = "achieve_id") val achieveId: Int,
    @ColumnInfo(name = "count") val count: Int,
)