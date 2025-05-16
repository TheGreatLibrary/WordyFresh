package com.sinya.projects.wordle.domain.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "offline_achievements")
data class OfflineAchievements(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "count") val count: Int,
)