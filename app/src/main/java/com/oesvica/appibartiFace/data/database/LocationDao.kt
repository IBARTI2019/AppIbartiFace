package com.oesvica.appibartiFace.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.oesvica.appibartiFace.data.model.location.Location
import com.oesvica.appibartiFace.data.model.location.Location.Companion.TABLE_NAME

@Dao
abstract class LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertLocations(vararg locations: Location)

    @Query("SELECT * FROM $TABLE_NAME")
    abstract fun findLocations(): LiveData<Location>

    @Query("SELECT * FROM $TABLE_NAME")
    abstract suspend fun findLocationsSynchronous(): List<Location>

    @Query("DELETE FROM $TABLE_NAME")
    abstract suspend fun deleteLocations()

    @Transaction
    open suspend fun replaceLocations(vararg locations: Location){
        deleteLocations()
        insertLocations(*locations)
    }
}