package com.oesvica.appibartiFace.di.module

import android.app.Application
import androidx.room.Room
import com.oesvica.appibartiFace.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RoomDatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(application: Application) =
        Room.databaseBuilder(application, AppDatabase::class.java, "TheAppIbartiFaceDBV3.2.db")
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideCategoryDao(appDatabase: AppDatabase) = appDatabase.categoryDao()

    @Singleton
    @Provides
    fun provideStatusDao(appDatabase: AppDatabase) = appDatabase.statusDao()

    @Singleton
    @Provides
    fun provideStandByDao(appDatabase: AppDatabase) = appDatabase.standByDao()

    @Singleton
    @Provides
    fun providePersonDao(appDatabase: AppDatabase) = appDatabase.personDao()

    @Singleton
    @Provides
    fun provideAsistenciaDao(appDatabase: AppDatabase) = appDatabase.asistenciaDao()

    @Singleton
    @Provides
    fun providePersonAsistenciaDao(appDatabase: AppDatabase) = appDatabase.personAsistenciaDao()

    @Singleton
    @Provides
    fun provideLocationDao(appDatabase: AppDatabase) = appDatabase.locationDao()

}