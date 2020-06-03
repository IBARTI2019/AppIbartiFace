package com.oesvica.appibartiFace.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.oesvica.appibartiFace.data.model.Category

@Database(entities = [Category::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
}