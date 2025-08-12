package com.sinya.projects.wordle.data.remote.supabase.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@Entity(tableName = "profiles")
data class Profiles(
    @PrimaryKey
    @SerialName("id")
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "nickname")
    @SerialName("nickname")
    val nickname: String,

    @ColumnInfo(name = "avatar_url")
    @SerialName("avatar_url")
    val avatarUrl: String,

    @ColumnInfo(name = "created_at")
    @SerialName("created_at")
    val createdAt: String,
)