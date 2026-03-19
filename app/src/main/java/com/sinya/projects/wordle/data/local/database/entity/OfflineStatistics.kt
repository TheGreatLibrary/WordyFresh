package com.sinya.projects.wordle.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "offline_statistics",
    foreignKeys = [
        ForeignKey(
            entity = ModesStatistics::class,
            parentColumns = ["id"],
            childColumns = ["mode_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["mode_id"]),
        Index(value = ["created_at"])
    ]
)
data class OfflineStatistics(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "mode_id") val modeId: Int,
    @ColumnInfo(name = "result") val result: Int = 0,
    @ColumnInfo(name = "time_game") val timeGame: Int = 0,
    @ColumnInfo(name = "word_length") val wordLength: Int?,
    @ColumnInfo(name = "word_lang") val wordLang: String?,
    @ColumnInfo(name = "try_number") val tryNumber: Int?,
    @ColumnInfo(name = "created_at") val createdAt: String,
)