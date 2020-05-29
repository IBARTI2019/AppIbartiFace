package com.oesvica.appibartiFace.data.repository

import com.oesvica.appibartiFace.data.model.Category
import com.oesvica.appibartiFace.data.model.Person
import com.oesvica.appibartiFace.data.model.Status
import io.reactivex.rxjava3.core.Completable

abstract class MaestrosRepository {

    abstract fun insertCategory(category: Category): Completable

    abstract fun updateCategory(category: Category): Completable

    abstract fun deleteCategory(category: Category): Completable

    abstract suspend fun findCategories(): List<Category>
    abstract suspend fun findStatuses(): List<Status>
    abstract suspend fun findPersons(): List<Person>

}