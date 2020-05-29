package com.oesvica.appibartiFace.ui.persons

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class PersonsProvider {

    @ContributesAndroidInjector
    abstract fun providePersonsFragment(): PersonsFragment

}