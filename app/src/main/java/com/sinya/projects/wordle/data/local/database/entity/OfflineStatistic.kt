package com.sinya.projects.wordle.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "offline_statistic",
    foreignKeys = [
        ForeignKey(
            entity = Modes::class,
            parentColumns = ["id"],
            childColumns = ["mode_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class OfflineStatistic(
    @PrimaryKey @ColumnInfo(name = "mode_id") val modeId: Int,
    @ColumnInfo(name = "count_game") val countGame: Int = 0,
    @ColumnInfo(name = "current_streak") val currentStreak: Int = 0,
    @ColumnInfo(name = "best_streak") val bestStreak: Int = 0,
    @ColumnInfo(name = "win_game") val winGame: Int = 0,
    @ColumnInfo(name = "sum_time") val sumTime: Long = 0,
    @ColumnInfo(name = "first_try") val firstTry: Int = 0,
    @ColumnInfo(name = "second_try") val secondTry: Int = 0,
    @ColumnInfo(name = "third_try") val thirdTry: Int = 0,
    @ColumnInfo(name = "fourth_try") val fourthTry: Int = 0,
    @ColumnInfo(name = "fifth_try") val fifthTry: Int = 0,
    @ColumnInfo(name = "sixth_try") val sixthTry: Int = 0
)