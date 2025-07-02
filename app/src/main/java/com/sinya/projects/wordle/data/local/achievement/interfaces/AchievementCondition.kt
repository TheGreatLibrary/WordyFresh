package com.sinya.projects.wordle.data.local.achievement.interfaces

import com.sinya.projects.wordle.data.local.achievement.AchievementTrigger
import com.sinya.projects.wordle.data.local.achievement.UserStats

interface AchievementCondition {
    fun isSatisfied(trigger: AchievementTrigger, stats: UserStats): Boolean
}
