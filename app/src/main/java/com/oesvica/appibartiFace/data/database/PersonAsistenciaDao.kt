package com.oesvica.appibartiFace.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.oesvica.appibartiFace.data.model.personAsistencia.PersonAsistencia
import com.oesvica.appibartiFace.utils.debug

@Dao
abstract class PersonAsistenciaDao : BaseDao<PersonAsistencia> {

    // Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertPersonAsistencias(vararg persons: PersonAsistencia)

    // Query
    @Query("SELECT * FROM ${PersonAsistencia.TABLE_NAME} WHERE (dateEntry BETWEEN :iniDate AND :endDate) AND isApto=:isApto")
    abstract fun findPersonAsistencias(iniDate: String, endDate: String, isApto: Boolean): LiveData<List<PersonAsistencia>>

    // Delete
    @Query("DELETE FROM ${PersonAsistencia.TABLE_NAME}")
    abstract suspend fun deleteAllPersonAsistencias()

    @Query("DELETE FROM ${PersonAsistencia.TABLE_NAME} WHERE (dateEntry BETWEEN :iniDate AND :endDate) AND isApto=:isApto")
    abstract suspend fun deletePersonAsistencias(iniDate: String, endDate: String, isApto: Boolean)

    @Transaction
    open suspend fun replacePersonAsistencias(iniDate: String, endDate: String, isApto: Boolean, vararg persons: PersonAsistencia) {
        debug("replacePersonAsistencias $iniDate $endDate $isApto ${persons.toList()}")
        deletePersonAsistencias(iniDate, endDate, isApto)
        insertPersonAsistencias(*persons)
    }


}