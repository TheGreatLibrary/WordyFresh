package com.sinya.projects.wordle.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "modes_statistics")
data class ModesStatistics(
    @PrimaryKey(autoGenerate = true) val id: Int
)

