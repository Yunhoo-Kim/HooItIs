package com.hooitis.hoo.hooitis.di.module

import com.hooitis.hoo.hooitis.di.ActivityScope
import com.hooitis.hoo.hooitis.ui.KeyboardModeActivity
import com.hooitis.hoo.hooitis.ui.QuizResultActivity
import dagger.Binds
import dagger.Module


@Module
@Suppress("unused")
abstract class QuizResultActivityModule {
    @ActivityScope
    @Binds
    abstract fun provideQuizResultActivity(activity: QuizResultActivity): QuizResultActivity
}