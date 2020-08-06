package com.oesvica.appibartiFace.ui.personAsistencia

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class DocsProvider {

    @ContributesAndroidInjector
    abstract fun provideDocsFragment(): DocsFragment

}