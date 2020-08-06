package com.oesvica.appibartiFace.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.oesvica.appibartiFace.data.model.person.Person

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

    @Query("DELETE FROM ${Person.TABLE_NAME} WHERE id=:personId")
    abstract suspend fun deletePerson(personId: String)

    @Transaction
    open suspend fun replacePersons(vararg persons: Person){
        deleteAllPersons()
        insertPersons(*persons)
    }

}