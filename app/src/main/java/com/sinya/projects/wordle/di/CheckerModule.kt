package com.sinya.projects.wordle.di

import com.sinya.projects.wordle.domain.checker.NetworkChecker
import com.sinya.projects.wordle.domain.checker.NetworkCheckerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CheckerModule {

    @Binds
    @Singleton
    abstract fun bindNetworkChecker(
        impl: NetworkCheckerImpl
    ): NetworkChecker
}