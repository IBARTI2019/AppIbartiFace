package com.oesvica.appibartiFace.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.oesvica.appibartiFace.data.model.StandBy
import com.oesvica.appibartiFace.data.model.currentDay

@Dao
abstract class StandByDao : BaseDao<StandBy> {

    // Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertStandBys(vararg standBys: StandBy)

    @Query("DELETE FROM ${StandBy.TABLE_NAME} WHERE date=:date")
    abstract suspend fun deleteStandBysByDate(date: String)

    @Transaction
    open suspend fun replaceStandBysByDate(date: String, vararg standBys: StandBy) {
        deleteStandBysByDate(date)
        insertStandBys(*standBys)
    }


    @Query("DELETE FROM ${StandBy.TABLE_NAME} WHERE client=:client AND date=:date")
    abstract suspend fun deleteStandBysByClientAndDate(client: String, date: String)

    @Transaction
    open suspend fun replaceStandBysByClientAndDate(
        client: String,
        date: String,
        vararg standBys: StandBy
    ) {
        deleteStandBysByClientAndDate(client, date)
        insertStandBys(*standBys)
    }

    // Query
    @Query("SELECT * FROM ${StandBy.TABLE_NAME}")
    abstract fun findAllStandBys(): LiveData<List<StandBy>>

    @Query("SELECT * FROM ${StandBy.TABLE_NAME} WHERE date=:today")
    abstract fun findStandBysByDate(today: String = currentDay().toString()): LiveData<List<StandBy>>


    @Query("SELECT * FROM ${StandBy.TABLE_NAME} WHERE client=:client AND date=:date")
    abstract fun findStandBysByClientAndDate(
        client: String,
        date: String = currentDay().toString()
    ): LiveData<List<StandBy>>

    // Delete
    @Query("DELETE FROM ${StandBy.TABLE_NAME}")
    abstract suspend fun deleteAllStandBys()

    @Query("DELETE FROM ${StandBy.TABLE_NAME} WHERE client=:client AND date=:date AND url=:url")
    abstract suspend fun deleteStandBy(client: String, date: String, url: String)

}