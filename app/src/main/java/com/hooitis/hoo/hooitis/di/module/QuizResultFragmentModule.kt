package com.hooitis.hoo.hooitis.di.module

import com.hooitis.hoo.hooitis.di.ActivityScope
import com.hooitis.hoo.hooitis.ui.KeyboardModeActivity
import com.hooitis.hoo.hooitis.ui.QuizResultActivity
import com.hooitis.hoo.hooitis.ui.QuizResultFragment
import dagger.Binds
import dagger.Module


@Module
@Suppress("unused")
abstract class QuizResultFragmentModule {
    @ActivityScope
    @Binds
    abstract fun provideQuizResultFragment(fragment : QuizResultFragment): QuizResultFragment
}