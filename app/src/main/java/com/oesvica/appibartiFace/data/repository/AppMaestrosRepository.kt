package com.oesvica.appibartiFace.data.repository

import androidx.lifecycle.LiveData
import com.oesvica.appibartiFace.data.database.CategoryDao
import com.oesvica.appibartiFace.data.database.PersonDao
import com.oesvica.appibartiFace.data.database.StandByDao
import com.oesvica.appibartiFace.data.database.StatusDao
import com.oesvica.appibartiFace.data.model.*
import com.oesvica.appibartiFace.data.remote.AppIbartiFaceApi
import com.oesvica.appibartiFace.utils.debug
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppMaestrosRepository
@Inject constructor(
    private val appIbartiFaceApi: AppIbartiFaceApi,
    private val categoryDao: CategoryDao,
    private val statusDao: StatusDao,
    private val standByDao: StandByDao,
    private val personDao: PersonDao
) : MaestrosRepository() {

    override fun findCategories(): LiveData<List<Category>> {
        return categoryDao.findCategories()
    }

    override suspend fun refreshCategories(): Result<Unit> {
        return mapToResult {
            val categories = appIbartiFaceApi.findCategories()
            debug("categories=$categories")
            categoryDao.deleteAllCategories()
            categoryDao.insertCategories(*categories.toTypedArray())
        }
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

    override suspend fun refreshStatuses(): Result<Unit> {
        return mapToResult {
            val statuses = appIbartiFaceApi.findStatuses()
            debug("statuses=$statuses")
            statusDao.deleteAllStatuses()
            statusDao.insertStatuses(*statuses.toTypedArray())
        }
    }

    override fun findStatuses(): LiveData<List<Status>> {
        return statusDao.findStatuses()
    }

    override suspend fun insertStatus(statusRequest: StatusRequest): Result<Status> {
        return mapToResult { appIbartiFaceApi.addStatus(statusRequest) }
    }

    override suspend fun updateStatus(status: Status): Result<Status> {
        return mapToResult { appIbartiFaceApi.updateStatus(status.id, StatusRequest(status.category, status.description)) }
    }

    override suspend fun deleteStatus(idStatus: String): Result<Unit> {
        return mapToResult { appIbartiFaceApi.deleteStatus(idStatus) }
    }

    override suspend fun refreshPersons(): Result<Unit> {
        return mapToResult {
            val persons = appIbartiFaceApi.findPersons()
            debug("persons=$persons")
            personDao.deleteAllPersons()
            personDao.insertPersons(*persons.toTypedArray())
        }
    }

    override fun findPersons(): LiveData<List<Person>> {
        return personDao.findAllPersons()
    }

    override suspend fun insertPerson(addPersonRequest: AddPersonRequest): Result<Person> {
        return mapToResult { appIbartiFaceApi.addPerson(addPersonRequest) }
    }

    override suspend fun updatePerson(personId: String, updatePersonRequest: UpdatePersonRequest): Result<Person> {
        return mapToResult { appIbartiFaceApi.updatePerson(personId, updatePersonRequest) }
    }

    override fun findCurrentDayStandBys(): LiveData<List<StandBy>> {
        return standByDao.findTodayStandBys()
    }

    override suspend fun refreshCurrentDayStandBys(): Result<Unit> {
        return mapToResult {
            val todayStandBys = appIbartiFaceApi.findStandBysCurrentDay()
            debug("todayStandBys=$todayStandBys")
            standByDao.deleteTodayStandBys()
            standByDao.insertStandBys(*todayStandBys.toTypedArray())
        }
    }

    override suspend fun deleteStandBy(client: String, date: String, url: String): Result<Unit> {
        return mapToResult { appIbartiFaceApi.deleteStandBy(client = client, date = date, deleteStandBy = DeleteStandBy(foto = url)) }
    }

    private suspend fun <T> mapToResult(sth: suspend() -> T): Result<T> {
        return try {
            Result(sth())
        } catch (e: Exception) {
            Result(error = e)
        }
    }
}