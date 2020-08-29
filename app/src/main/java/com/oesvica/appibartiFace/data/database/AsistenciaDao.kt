package com.oesvica.appibartiFace.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.oesvica.appibartiFace.data.model.asistencia.Asistencia

@Dao
abstract class AsistenciaDao {

    // Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAsistencias(vararg asistencias: Asistencia)

    // Query
    @Query("SELECT * FROM ${Asistencia.TABLE_NAME} WHERE date BETWEEN :iniDate AND :endDate")
    abstract fun findAsistencias(iniDate: String, endDate: String): LiveData<List<Asistencia>>

    // Delete
    @Query("DELETE FROM ${Asistencia.TABLE_NAME} WHERE date BETWEEN :iniDate AND :endDate")
    abstract suspend fun deleteAsistencias(iniDate: String, endDate: String)

    @Transaction
    open suspend fun replaceAsistencias(
        iniDate: String,
        endDate: String,
        vararg asistencias: Asistencia
    ){
        deleteAsistencias(iniDate, endDate)
        insertAsistencias(*asistencias)
    }

}