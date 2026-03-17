package com.sinya.projects.wordle.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "mode_statistics_translations",
    primaryKeys = ["mode_id", "lang"],
    foreignKeys = [
        ForeignKey(
            entity = ModesStatistics::class,
            parentColumns = ["id"],
            childColumns = ["mode_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["mode_id"])
    ]
)
data class ModeStatisticsTranslations(
    @ColumnInfo(name = "mode_id") val modeId: Int,
    @ColumnInfo(name = "lang") val lang: String,
    @ColumnInfo(name = "name") val name: String
)