package com.sinya.projects.wordle.domain.model.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SyncAchievements(
    val id: Int,
    @SerialName("user_id") val userId: String,
    val count: Int,
    @SerialName("updated_at") val updatedAt: String
)