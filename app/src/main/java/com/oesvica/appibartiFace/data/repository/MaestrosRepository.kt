package com.oesvica.appibartiFace.data.repository

import com.oesvica.appibartiFace.data.model.Category
import com.oesvica.appibartiFace.data.model.Person
import com.oesvica.appibartiFace.data.model.Status
import com.oesvica.appibartiFace.data.model.Result
import io.reactivex.rxjava3.core.Completable

abstract class MaestrosRepository {

    abstract suspend fun findCategories(): Result<List<Category>>
    abstract suspend fun insertCategory(description: String): Result<Category>
    abstract suspend fun updateCategory(category: Category): Result<Category>
    abstract suspend fun deleteCategory(idCategory: String): Result<Unit>

    abstract suspend fun findStatuses(): Result<List<Status>>
    abstract suspend fun findPersons(): Result<List<Person>>

}