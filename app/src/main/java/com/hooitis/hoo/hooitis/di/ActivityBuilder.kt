package com.hooitis.hoo.hooitis.di

import com.hooitis.hoo.hooitis.di.module.MainActivityModule
import com.hooitis.hoo.hooitis.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
@Suppress("unused")
abstract class ActivityBuilder{
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun bindMainActivityModule(): MainActivity

}