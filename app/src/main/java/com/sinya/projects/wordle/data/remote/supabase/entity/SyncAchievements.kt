package com.sinya.projects.wordle.data.remote.supabase.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.sinya.projects.wordle.data.local.entity.Achievements
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "sync_achievements",
    primaryKeys = ["user_id", "achieve_id"],
    foreignKeys = [
        ForeignKey(
            entity = Profiles::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Achievements::class,
            parentColumns = ["id"],
            childColumns = ["achieve_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SyncAchievements(
    @SerialName("achieve_id") @ColumnInfo(name = "achieve_id") val achieveId: Int,
    @SerialName("user_id") @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "count") val count: Int = 0,
    @SerialName("updated_at") @ColumnInfo(name = "updated_at") val updatedAt: String
)