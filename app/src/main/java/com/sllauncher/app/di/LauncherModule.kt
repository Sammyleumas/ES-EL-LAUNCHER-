package com.sllauncher.app.di

import com.sllauncher.app.assistant.DefaultLauncherActionBridge
import com.sllauncher.app.assistant.LauncherActionBridge
import com.sllauncher.app.commands.FeatureCommandHandlers
import com.sllauncher.app.commands.FeatureCommandHandlersImpl
import com.sllauncher.app.launcher.AndroidDefaultHomeChecker
import com.sllauncher.app.launcher.DefaultHomeChecker
import com.sllauncher.app.launcher.SystemTimeSource
import com.sllauncher.app.launcher.TimeSource
import com.sllauncher.app.news.NewsProvider
import com.sllauncher.app.news.RssNewsProvider
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

    @Binds
    abstract fun bindFeatureCommands(impl: FeatureCommandHandlersImpl): FeatureCommandHandlers

    @Binds
    abstract fun bindNewsProvider(impl: RssNewsProvider): NewsProvider

    @Binds
    abstract fun bindAssistantBridge(impl: DefaultLauncherActionBridge): LauncherActionBridge
}
