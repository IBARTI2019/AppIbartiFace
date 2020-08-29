package com.oesvica.appibartiFace.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.oesvica.appibartiFace.data.model.asistencia.Asistencia
import com.oesvica.appibartiFace.data.model.category.Category
import com.oesvica.appibartiFace.data.model.location.Location
import com.oesvica.appibartiFace.data.model.person.Person
import com.oesvica.appibartiFace.data.model.personAsistencia.PersonAsistencia
import com.oesvica.appibartiFace.data.model.standby.StandBy
import com.oesvica.appibartiFace.data.model.status.Status

@Database(entities = [Category::class, Status::class, StandBy::class, Person::class, Asistencia::class, PersonAsistencia::class, Location::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun statusDao(): StatusDao
    abstract fun standByDao(): StandByDao
    abstract fun personDao(): PersonDao
    abstract fun asistenciaDao(): AsistenciaDao
    abstract fun personAsistenciaDao(): PersonAsistenciaDao
    abstract fun locationDao(): LocationDao

    suspend fun clearTables(){
        //categoryDao().deleteAllCategories()
    }
}