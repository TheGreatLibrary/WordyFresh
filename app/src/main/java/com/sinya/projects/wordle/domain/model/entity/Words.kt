package com.sinya.projects.wordle.domain.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "words")
data class Words(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val word: String,
    val length: Int,
    val language: String,
    @ColumnInfo(defaultValue = "0") val rating: Int = 0
)