package com.oesvica.appibartiFace.data.repository

import com.oesvica.appibartiFace.data.model.*
import com.oesvica.appibartiFace.data.remote.AppIbartiFaceApi
import io.reactivex.rxjava3.core.Completable
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppMaestrosRepository
@Inject constructor(
    private val appIbartiFaceApi: AppIbartiFaceApi
) : MaestrosRepository() {

    override suspend fun findCategories(): Result<List<Category>> {

        return mapToResult { appIbartiFaceApi.findCategories() }
    }

    override suspend fun insertCategory(description: String): Result<Category> {
        return mapToResult { appIbartiFaceApi.addCategory(CategoryRequest(description)) }
    }

    override suspend fun updateCategory(category: Category): Result<Category> {
        return mapToResult { appIbartiFaceApi.updateCategory(category.id, CategoryRequest(category.description)) }
    }

    override suspend fun deleteCategory(idCategory: String): Result<Unit> {
        return mapToResult { appIbartiFaceApi.deleteCategory(idCategory) }
    }

    override suspend fun findStatuses(): Result<List<Status>> {
        return mapToResult { appIbartiFaceApi.findStatuses() }
    }

    override suspend fun findPersons(): Result<List<Person>> {
        return mapToResult { appIbartiFaceApi.findPersons() }
    }

    private suspend fun <T> mapToResult(sth: suspend() -> T): Result<T> {
        return try {
            Result(sth())
        } catch (e: Exception) {
            Result(error = e)
        }
    }
}