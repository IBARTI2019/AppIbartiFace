package com.oesvica.appibartiFace.ui.asistencia

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class AsistenciaProvider {

    @ContributesAndroidInjector
    abstract fun provideAsistenciaFragment(): AsistenciaFragment

}