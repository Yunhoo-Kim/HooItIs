package com.hooitis.hoo.hooitis.di.module

import com.hooitis.hoo.hooitis.di.ActivityScope
import com.hooitis.hoo.hooitis.ui.KeyboardModeActivity
import dagger.Binds
import dagger.Module


@Module
@Suppress("unused")
abstract class KeyboardModeActivityModule {
    @ActivityScope
    @Binds
    abstract fun provideMainActivity(activity: KeyboardModeActivity): KeyboardModeActivity
}