package com.oesvica.appibartiFace.ui.standby

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class StandByProvider {

    @ContributesAndroidInjector
    abstract fun provideStandByFragment(): StandByFragment

}