package com.sinya.projects.wordle.domain.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "modes")
data class Modes(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "name") val name: String
)