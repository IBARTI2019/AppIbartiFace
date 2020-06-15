package com.oesvica.appibartiFace.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.oesvica.appibartiFace.data.model.Category

@Dao
abstract class CategoryDao: BaseDao<Category> {

    // Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertCategories(vararg categories: Category)

    // Query
    @Query("SELECT * FROM ${Category.TABLE_NAME}")
    abstract fun findCategories(): LiveData<List<Category>>

    @Query("SELECT * FROM ${Category.TABLE_NAME}")
    abstract suspend fun findCategoriesBlocking(): List<Category>

    // Delete
    @Query("DELETE FROM ${Category.TABLE_NAME}")
    abstract suspend fun deleteAllCategories()
}