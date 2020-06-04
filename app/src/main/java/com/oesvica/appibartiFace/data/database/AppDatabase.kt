package com.oesvica.appibartiFace.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.oesvica.appibartiFace.data.model.Category
import com.oesvica.appibartiFace.data.model.Person
import com.oesvica.appibartiFace.data.model.StandBy
import com.oesvica.appibartiFace.data.model.Status

@Database(entities = [Category::class, Status::class, StandBy::class, Person::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun statusDao(): StatusDao
    abstract fun standByDao(): StandByDao
    abstract fun personDao(): PersonDao
}