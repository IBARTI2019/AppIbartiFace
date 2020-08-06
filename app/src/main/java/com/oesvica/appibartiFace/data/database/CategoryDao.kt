package com.oesvica.appibartiFace.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.oesvica.appibartiFace.data.model.category.Category

@Dao
abstract class CategoryDao: BaseDao<Category> {

    // Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertCategories(vararg categories: Category)

    // Query
    @Query("SELECT * FROM ${Category.TABLE_NAME}")
    abstract fun findCategories(): LiveData<List<Category>>

    @Query("SELECT * FROM ${Category.TABLE_NAME}")
    abstract suspend fun findCategoriesSynchronous(): List<Category>

    // Delete
    @Query("DELETE FROM ${Category.TABLE_NAME}")
    abstract suspend fun deleteAllCategories()

    @Transaction
    open suspend fun replaceCategories(vararg categories: Category){
        deleteAllCategories()
        insertCategories(*categories)
    }
}