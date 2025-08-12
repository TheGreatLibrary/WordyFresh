package com.sinya.projects.wordle.data.remote.supabase.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.sinya.projects.wordle.data.local.entity.Modes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "sync_statistic",
    primaryKeys = ["user_id", "mode_id"],
    foreignKeys = [
        ForeignKey(
            entity = Modes::class,
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
    ]
)
data class SyncStatistic(
    @ColumnInfo(name = "user_id")
    @SerialName("user_id")
    val userId: String,

    @ColumnInfo(name = "mode_id")
    @SerialName("mode_id")
    val modeId: Int,

    @ColumnInfo(name = "count_game")
    @SerialName("count_game")
    val countGame: Int = 0,

    @ColumnInfo(name = "current_streak")
    @SerialName("current_streak")
    val currentStreak: Int = 0,

    @ColumnInfo(name = "best_streak")
    @SerialName("best_streak")
    val bestStreak: Int = 0,

    @ColumnInfo(name = "win_game")
    @SerialName("win_game")
    val winGame: Int = 0,

    @ColumnInfo(name = "sum_time")
    @SerialName("sum_time")
    val sumTime: Long = 0,

    @ColumnInfo(name = "first_try")
    @SerialName("first_try")
    val firstTry: Int = 0,

    @ColumnInfo(name = "second_try")
    @SerialName("second_try")
    val secondTry: Int = 0,

    @ColumnInfo(name = "third_try")
    @SerialName("third_try")
    val thirdTry: Int = 0,

    @ColumnInfo(name = "fourth_try")
    @SerialName("fourth_try")
    val fourthTry: Int = 0,

    @ColumnInfo(name = "fifth_try")
    @SerialName("fifth_try")
    val fifthTry: Int = 0,

    @ColumnInfo(name = "sixth_try")
    @SerialName("sixth_try")
    val sixthTry: Int = 0,

    @ColumnInfo(name = "updated_at")
    @SerialName("updated_at")
    val updatedAt: String
)