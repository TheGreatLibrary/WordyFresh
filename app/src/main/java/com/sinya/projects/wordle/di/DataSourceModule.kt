package com.sinya.projects.wordle.di

import com.sinya.projects.wordle.domain.source.AvatarLocalDataSource
import com.sinya.projects.wordle.domain.source.AvatarRemoteDataSource
import com.sinya.projects.wordle.utils.BitmapImageCompressor
import com.sinya.projects.wordle.domain.source.DefinitionDataSource
import com.sinya.projects.wordle.domain.source.FileAvatarLocalDataSource
import com.sinya.projects.wordle.utils.ImageCompressor
import com.sinya.projects.wordle.domain.source.SupabaseAchievementDataSource
import com.sinya.projects.wordle.domain.source.SupabaseAchievementDataSourceImpl
import com.sinya.projects.wordle.domain.source.SupabaseAuthDataSource
import com.sinya.projects.wordle.domain.source.SupabaseAuthDataSourceImpl
import com.sinya.projects.wordle.domain.source.SupabaseAvatarDataSource
import com.sinya.projects.wordle.domain.source.SupabaseDictionaryDataSource
import com.sinya.projects.wordle.domain.source.SupabaseDictionaryDataSourceImpl
import com.sinya.projects.wordle.domain.source.SupabaseProfileDataSource
import com.sinya.projects.wordle.domain.source.SupabaseProfileDataSourceImpl
import com.sinya.projects.wordle.domain.source.SupabaseStatisticsDataSource
import com.sinya.projects.wordle.domain.source.SupabaseStatisticsDataSourceImpl
import com.sinya.projects.wordle.domain.source.WikipediaDataSource
import com.sinya.projects.wordle.domain.source.WiktionaryDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Binds
    abstract fun bindSupabaseAuthDataSource(
        impl: SupabaseAuthDataSourceImpl
    ): SupabaseAuthDataSource

    @Binds
    abstract fun bindSupabaseStatisticsDataSource(
        impl: SupabaseStatisticsDataSourceImpl
    ): SupabaseStatisticsDataSource

    @Binds
    abstract fun bindSupabaseAchievementDataSource(
        impl: SupabaseAchievementDataSourceImpl
    ): SupabaseAchievementDataSource

    @Binds
    abstract fun bindSupabaseDictionaryDataSource(
        impl: SupabaseDictionaryDataSourceImpl
    ): SupabaseDictionaryDataSource

    @Binds
    abstract fun bindSupabaseProfileDataSource(
        impl: SupabaseProfileDataSourceImpl
    ): SupabaseProfileDataSource

    // аватар

    @Binds
    abstract fun bindAvatarRemoteDataSource(
        impl: SupabaseAvatarDataSource
    ): AvatarRemoteDataSource

    @Binds
    abstract fun bindAvatarLocalDataSource(
        impl: FileAvatarLocalDataSource
    ): AvatarLocalDataSource

    // доп метод

    @Binds
    abstract fun bindDictionaryDataSource(
        impl: WikipediaDataSource
    ): DefinitionDataSource

    @Binds
    abstract fun bindDictionaryWiktionaryDataSource(
        impl: WiktionaryDataSource
    ): DefinitionDataSource

    @Binds
    abstract fun bindImageCompressor(
        impl: BitmapImageCompressor
    ): ImageCompressor
}