package com.sinya.projects.wordle.data.achievement.interfaces

import com.sinya.projects.wordle.data.achievement.AchievementTrigger
import com.sinya.projects.wordle.data.achievement.UserStats

interface AchievementCondition {
    fun isSatisfied(trigger: AchievementTrigger, stats: UserStats): Boolean
}
