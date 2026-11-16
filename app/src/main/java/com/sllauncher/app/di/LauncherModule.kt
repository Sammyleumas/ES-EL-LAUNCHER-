package com.sllauncher.app.di

import com.sllauncher.app.launcher.AndroidDefaultHomeChecker
import com.sllauncher.app.launcher.DefaultHomeChecker
import com.sllauncher.app.launcher.SystemTimeSource
import com.sllauncher.app.launcher.TimeSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Binds launcher-level interfaces to their Android implementations. */
@Module
@InstallIn(SingletonComponent::class)
abstract class LauncherModule {

    @Binds
    abstract fun bindDefaultHomeChecker(impl: AndroidDefaultHomeChecker): DefaultHomeChecker

    @Binds
    abstract fun bindTimeSource(impl: SystemTimeSource): TimeSource
}
