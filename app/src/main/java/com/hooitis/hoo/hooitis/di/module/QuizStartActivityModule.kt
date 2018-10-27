package com.hooitis.hoo.hooitis.di.module

import com.hooitis.hoo.hooitis.di.ActivityScope
import com.hooitis.hoo.hooitis.ui.MainActivity
import com.hooitis.hoo.hooitis.ui.QuizStartActivity
import com.hooitis.hoo.hooitis.ui.SplashActivity
import dagger.Binds
import dagger.Module


@Module
@Suppress("unused")
abstract class QuizStartActivityModule {
    @ActivityScope
    @Binds
    abstract fun provideQuizStartActivity(activity: QuizStartActivity): QuizStartActivity
}