/*
 * Copyright (C) 2018 Sneyder Angulo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oesvica.appibartiFace.di.module

import AppCoroutineContextProvider
import android.app.Application
import android.content.Context
import com.oesvica.appibartiFace.utils.schedulers.AppSchedulerProvider
import com.oesvica.appibartiFace.utils.schedulers.SchedulerProvider
import com.sneyder.utils.coroutines.CoroutineContextProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module(includes = [(ViewModelModule::class), (RepositoriesModule::class), (RetrofitModule::class)])
abstract class AppModule {

    @Binds
    @Reusable
    abstract fun provideContext(application: Application): Context

    @Module
    companion object {

        @Provides
        @JvmStatic
        @Reusable
        fun provideSchedulerProvider(): SchedulerProvider = AppSchedulerProvider()

        @Provides
        @JvmStatic
        @Reusable
        fun provideCoroutineContextProvider(): CoroutineContextProvider = AppCoroutineContextProvider()

    }
}