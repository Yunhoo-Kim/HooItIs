package com.hooitis.hoo.hooitis.di.module

import com.hooitis.hoo.hooitis.di.ActivityScope
import com.hooitis.hoo.hooitis.ui.BeforeQuizActivity
import com.hooitis.hoo.hooitis.ui.MainActivity
import com.hooitis.hoo.hooitis.ui.QuizStartActivity
import com.hooitis.hoo.hooitis.ui.SplashActivity
import dagger.Binds
import dagger.Module


@Module
@Suppress("unused")
abstract class BeforeQuizActivityModule {
    @ActivityScope
    @Binds
    abstract fun provideBeforeQuizActivity(activity: BeforeQuizActivity): BeforeQuizActivity
}