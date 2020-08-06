package com.oesvica.appibartiFace.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

//val MIGRATION_1_2 = object : Migration(1, 2) {
//    override fun migrate(database: SupportSQLiteDatabase) {
//        database.execSQL("ALTER TABLE Person ADD COLUMN photo TEXT NOT NULL DEFAULT ''")
//        database.execSQL("ALTER TABLE Person ADD COLUMN names TEXT")
//        database.execSQL("ALTER TABLE Person ADD COLUMN surnames TEXT")
//        database.execSQL("ALTER TABLE Person ADD COLUMN dateBorn TEXT NOT NULL DEFAULT ''")
//    }
//}
//
//val MIGRATION_2_3 = object : Migration(2, 3) {
//    override fun migrate(database: SupportSQLiteDatabase) {
//        database.execSQL("CREATE TABLE PersonAsistencia(`cedula` TEXT NOT NULL DEFAULT '', `names` TEXT, surnames TEXT, `dateEntry` TEXT NOT NULL DEFAULT '', `timeEntry` TEXT NOT NULL DEFAULT '', `photoEntry` TEXT NOT NULL DEFAULT '', isApto INTEGER, PRIMARY KEY(`cedula`))")
//    }
//}