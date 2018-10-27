package com.hooitis.hoo.hooitis.di.module

import com.hooitis.hoo.hooitis.di.ActivityScope
import com.hooitis.hoo.hooitis.ui.MainActivity
import com.hooitis.hoo.hooitis.ui.SplashActivity
import dagger.Binds
import dagger.Module


@Module
@Suppress("unused")
abstract class SplashActivityModule {
    @ActivityScope
    @Binds
    abstract fun provideSplashActivity(activity: SplashActivity): SplashActivity
}