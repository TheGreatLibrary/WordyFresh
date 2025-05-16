package com.sinya.projects.wordle.domain.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@Entity(
    tableName = "sync_dictionary",
    foreignKeys = [
        ForeignKey(
            entity = Profiles::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Words::class,
            parentColumns = ["id"],
            childColumns = ["word_id"],
            onDelete = ForeignKey.CASCADE
        )
  ],
)
data class SyncDictionary(
    @PrimaryKey
    @SerialName("id")
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "user_id")
    @SerialName("user_id")
    val userId: String,

    @ColumnInfo(name = "word_id")
    @SerialName("word_id")
    val wordId: String,

    @ColumnInfo(name = "description")
    @SerialName("description")
    val description: String,

    @ColumnInfo(name = "updated_at")
    @SerialName("updated_at")
    val updatedAt: String
)