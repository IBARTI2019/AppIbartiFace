package com.oesvica.appibartiFace.data.repository

import com.oesvica.appibartiFace.data.model.Category
import com.oesvica.appibartiFace.data.model.Person
import com.oesvica.appibartiFace.data.model.Result
import com.oesvica.appibartiFace.data.model.Status
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

    override fun insertCategory(category: Category): Completable {
        TODO("Not yet implemented")
    }

    override fun updateCategory(category: Category): Completable {
        TODO("Not yet implemented")
    }

    override fun deleteCategory(category: Category): Completable {
        TODO("Not yet implemented")
    }

    override suspend fun findCategories(): Result<List<Category>> {
        return mapToResult { appIbartiFaceApi.findCategories() }
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