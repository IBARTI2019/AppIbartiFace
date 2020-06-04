package com.oesvica.appibartiFace.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.oesvica.appibartiFace.data.model.StandBy
import com.oesvica.appibartiFace.utils.currentDay

@Dao
abstract class StandByDao : BaseDao<StandBy> {

    // Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertStandBys(vararg standBys: StandBy)

    // Query
    @Query("SELECT * FROM ${StandBy.TABLE_NAME}")
    abstract fun findAllStandBys(): LiveData<List<StandBy>>

    @Query("SELECT * FROM ${StandBy.TABLE_NAME} WHERE date=:today")
    abstract fun findStandBysByDate(today: String = currentDay()): LiveData<List<StandBy>>

    @Query("SELECT * FROM ${StandBy.TABLE_NAME} WHERE client=:client AND date=:date")
    abstract fun findStandBysByClientAndDate(client: String, date: String = currentDay()): LiveData<List<StandBy>>

    // Delete
    @Query("DELETE FROM ${StandBy.TABLE_NAME}")
    abstract suspend fun deleteAllStandBys()

    @Query("DELETE FROM ${StandBy.TABLE_NAME} WHERE date=:today")
    abstract suspend fun deleteStandBysByDate(today: String = currentDay())

    @Query("DELETE FROM ${StandBy.TABLE_NAME} WHERE client=:client AND date=:today")
    abstract suspend fun deleteStandBysByClientAndDate(client: String, today: String = currentDay())

}