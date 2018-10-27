package com.hooitis.hoo.hooitis.di

import com.hooitis.hoo.hooitis.di.module.BeforeQuizActivityModule
import com.hooitis.hoo.hooitis.di.module.MainActivityModule
import com.hooitis.hoo.hooitis.di.module.QuizStartActivityModule
import com.hooitis.hoo.hooitis.di.module.SplashActivityModule
import com.hooitis.hoo.hooitis.ui.BeforeQuizActivity
import com.hooitis.hoo.hooitis.ui.MainActivity
import com.hooitis.hoo.hooitis.ui.QuizStartActivity
import com.hooitis.hoo.hooitis.ui.SplashActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
@Suppress("unused")
abstract class ActivityBuilder{
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun bindMainActivityModule(): MainActivity

    @ContributesAndroidInjector(modules = [SplashActivityModule::class])
    abstract fun bindSplashActivityModule(): SplashActivity

    @ContributesAndroidInjector(modules = [QuizStartActivityModule::class])
    abstract fun bindQuizStartActivityModule(): QuizStartActivity

    @ContributesAndroidInjector(modules = [BeforeQuizActivityModule::class])
    abstract fun bindBeforeQuizActivityModule(): BeforeQuizActivity
}