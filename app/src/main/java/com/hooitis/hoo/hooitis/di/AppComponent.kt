package com.hooitis.hoo.hooitis.di

import com.hooitis.hoo.hooitis.HooItIsApp
import com.hooitis.hoo.hooitis.di.module.DaoModule
import com.hooitis.hoo.hooitis.di.module.NetworkModule
import com.hooitis.hoo.hooitis.di.module.ViewModelModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

//    ActivityBuilder::class, DaoModule::class, ViewModelModule::class])
@Suppress(names = ["unchecked", "unsafe", "unused"])
@Singleton
@Component(modules = [AndroidSupportInjectionModule::class, AppModule::class, NetworkModule::class,
    ActivityBuilder::class, DaoModule::class, ViewModelModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder{
        @BindsInstance
        fun application(app: HooItIsApp): Builder
        fun appModule(appModule: AppModule): Builder
        fun networkModule(networkModule: NetworkModule): Builder
        fun build(): AppComponent
    }

    fun inject(app: HooItIsApp)
}

