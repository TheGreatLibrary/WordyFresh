package com.sinya.projects.wordle.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "modes")
data class Modes(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name") val name: String
)