package com.hooitis.hoo.hooitis.di.module

import com.hooitis.hoo.hooitis.di.ActivityScope
import com.hooitis.hoo.hooitis.ui.MainActivity
import dagger.Binds
import dagger.Module


@Module
@Suppress("unused")
abstract class MainActivityModule {
    @ActivityScope
    @Binds
    abstract fun provideMainActivity(activity: MainActivity): MainActivity
}