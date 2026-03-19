package com.sinya.projects.wordle.data.remote.supabase.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.sinya.projects.wordle.data.local.database.entity.ModesStatistics
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "sync_statistics",
    foreignKeys = [
        ForeignKey(
            entity = ModesStatistics::class,
            parentColumns = ["id"],
            childColumns = ["mode_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Profiles::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["mode_id"]),
        Index(value = ["user_id"]),
        Index(value = ["created_at"])
    ]
)
data class SyncStatistics(
    @PrimaryKey val id: String,

    @ColumnInfo(name = "user_id")
    @SerialName("user_id")
    val userId: String,

    @ColumnInfo(name = "mode_id")
    @SerialName("mode_id")
    val modeId: Int,

    @ColumnInfo(name = "result")
    @SerialName("result")
    val result: Int = 0,

    @ColumnInfo(name = "time_game")
    @SerialName("time_game")
    val timeGame: Int = 0,

    @ColumnInfo(name = "word_length")
    @SerialName("word_length")
    val wordLength: Int?,

    @ColumnInfo(name = "word_lang")
    @SerialName("word_lang")
    val wordLang: String?,

    @ColumnInfo(name = "try_number")
    @SerialName("try_number")
    val tryNumber: Int?,

    @ColumnInfo(name = "created_at")
    @SerialName("created_at")
    val createdAt: String,

    @ColumnInfo(name = "updated_at")
    @SerialName("updated_at")
    val updatedAt: String
)
