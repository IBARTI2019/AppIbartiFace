package com.oesvica.appibartiFace.ui.standby

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class StandByProvider {

    @ContributesAndroidInjector
    abstract fun provideStandByFragment(): StandByFragment

}