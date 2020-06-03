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
    abstract fun insertCategories(vararg categories: Category)

    // Query
    @Query("SELECT * FROM ${Category.TABLE_NAME}")
    abstract fun findCategories(deck: String): LiveData<List<Category>>

}