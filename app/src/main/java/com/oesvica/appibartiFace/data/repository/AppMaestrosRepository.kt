package com.oesvica.appibartiFace.data.repository

import com.oesvica.appibartiFace.data.model.Category
import com.oesvica.appibartiFace.data.model.Person
import com.oesvica.appibartiFace.data.model.Status
import com.oesvica.appibartiFace.data.remote.AppIbartiFaceApi
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppMaestrosRepository
@Inject constructor(
    private val appIbartiFaceApi: AppIbartiFaceApi
) : MaestrosRepository() {

    override fun insertCategory(category: Category): Completable {
        TODO("Not yet implemented")
    }

    override fun updateCategory(category: Category): Completable {
        TODO("Not yet implemented")
    }

    override fun deleteCategory(category: Category): Completable {
        TODO("Not yet implemented")
    }

    override suspend fun findCategories(): List<Category> {
        return appIbartiFaceApi.findCategories()
    }

    override suspend fun findStatuses(): List<Status> {
        return appIbartiFaceApi.findStatuses()
    }

    override suspend fun findPersons(): List<Person> {
        return appIbartiFaceApi.findPersons()
    }
}