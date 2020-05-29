package com.oesvica.appibartiFace.ui.statuses

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class StatusesProvider {

    @ContributesAndroidInjector
    abstract fun provideStatusesFragment(): StatusesFragment

}