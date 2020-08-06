package com.oesvica.appibartiFace.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.oesvica.appibartiFace.data.model.status.Status


@Dao
abstract class StatusDao: BaseDao<Status> {

    // Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertStatuses(vararg statuses: Status)

    // Query
    @Query("SELECT * FROM ${Status.TABLE_NAME}")
    abstract fun findStatuses(): LiveData<List<Status>>

    @Query("SELECT * FROM ${Status.TABLE_NAME}")
    abstract suspend fun findStatusesSynchronous(): List<Status>

    // Delete
    @Query("DELETE FROM ${Status.TABLE_NAME}")
    abstract suspend fun deleteAllStatuses()

    @Transaction
    open suspend fun replaceStatuses(vararg statuses: Status){
        deleteAllStatuses()
        insertStatuses(*statuses)
    }
}