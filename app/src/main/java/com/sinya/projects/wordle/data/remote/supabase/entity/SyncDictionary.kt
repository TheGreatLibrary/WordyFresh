package com.sinya.projects.wordle.data.remote.supabase.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.sinya.projects.wordle.data.local.database.entity.Words
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "sync_dictionary",
    primaryKeys = ["user_id", "word_id"],
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
    @ColumnInfo(name = "user_id")
    @SerialName("user_id")
    val userId: String,

    @ColumnInfo(name = "word_id")
    @SerialName("word_id")
    val wordId: Int,

    @ColumnInfo(name = "description")
    @SerialName("description")
    val description: String,

    @ColumnInfo(name = "updated_at")
    @SerialName("updated_at")
    val updatedAt: String
)