package com.oesvica.appibartiFace.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.oesvica.appibartiFace.data.model.Person

@Dao
abstract class PersonDao: BaseDao<Person> {

    // Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertPersons(vararg persons: Person)

    // Query
    @Query("SELECT * FROM ${Person.TABLE_NAME}")
    abstract fun findAllPersons(): LiveData<List<Person>>

    // Delete
    @Query("DELETE FROM ${Person.TABLE_NAME}")
    abstract suspend fun deleteAllPersons()

}