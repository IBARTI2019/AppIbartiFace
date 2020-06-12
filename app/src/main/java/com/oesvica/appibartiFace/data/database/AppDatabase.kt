package com.oesvica.appibartiFace.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.oesvica.appibartiFace.data.model.*

@Database(entities = [Category::class, Status::class, StandBy::class, Person::class, Asistencia::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun statusDao(): StatusDao
    abstract fun standByDao(): StandByDao
    abstract fun personDao(): PersonDao
    abstract fun asistenciaDao(): AsistenciaDao
}