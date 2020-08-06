package com.oesvica.appibartiFace.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database : SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Person ADD COLUMN photo TEXT NOT NULL DEFAULT ''")
        database.execSQL("ALTER TABLE Person ADD COLUMN names TEXT")
        database.execSQL("ALTER TABLE Person ADD COLUMN surnames TEXT")
        database.execSQL("ALTER TABLE Person ADD COLUMN dateBorn TEXT NOT NULL DEFAULT ''")
    }
}