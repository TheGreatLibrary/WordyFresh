package com.sinya.projects.wordle.di

import com.sinya.projects.wordle.domain.repository.AchievementRepository
import com.sinya.projects.wordle.domain.repository.AchievementRepositoryImpl
import com.sinya.projects.wordle.domain.repository.AvatarRepository
import com.sinya.projects.wordle.domain.repository.AvatarRepositoryImpl
import com.sinya.projects.wordle.domain.repository.DictionaryRepository
import com.sinya.projects.wordle.domain.repository.DictionaryRepositoryImpl
import com.sinya.projects.wordle.domain.repository.ProfileRepository
import com.sinya.projects.wordle.domain.repository.ProfileRepositoryImpl
import com.sinya.projects.wordle.domain.repository.StatisticRepository
import com.sinya.projects.wordle.domain.repository.StatisticRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {

    @Binds
    @Singleton
    abstract fun bindAchievementRepository(
        impl: AchievementRepositoryImpl
    ) : AchievementRepository

    @Binds
    @Singleton
    abstract fun bindDictionaryRepository(
        impl: DictionaryRepositoryImpl
    ) : DictionaryRepository

    @Binds
    @Singleton
    abstract fun bindStatisticRepository(
        impl: StatisticRepositoryImpl
    ) : StatisticRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        impl: ProfileRepositoryImpl
    ) : ProfileRepository

    @Binds
    @Singleton
    abstract fun bindAvatarRepository(
        impl: AvatarRepositoryImpl
    ): AvatarRepository
}