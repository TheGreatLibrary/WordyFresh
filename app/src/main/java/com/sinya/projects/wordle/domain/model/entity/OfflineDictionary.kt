package com.sinya.projects.wordle.domain.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "offline_dictionary",
    foreignKeys = [
        ForeignKey(
            entity = Words::class,
            parentColumns = ["id"],
            childColumns = ["word_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class OfflineDictionary(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "word_id") val wordId: String,
    @ColumnInfo(name = "description") val description: String
)