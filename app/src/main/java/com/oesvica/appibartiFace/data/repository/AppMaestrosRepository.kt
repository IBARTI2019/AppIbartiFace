package com.oesvica.appibartiFace.data.repository

import androidx.lifecycle.LiveData
import com.oesvica.appibartiFace.data.database.*
import com.oesvica.appibartiFace.data.model.*
import com.oesvica.appibartiFace.data.preferences.PreferencesHelper
import com.oesvica.appibartiFace.data.remote.AppIbartiFaceApi
import com.oesvica.appibartiFace.utils.debug
import java.lang.Exception
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppMaestrosRepository
@Inject constructor(
    private val appIbartiFaceApi: AppIbartiFaceApi,
    private val categoryDao: CategoryDao,
    private val statusDao: StatusDao,
    private val standByDao: StandByDao,
    private val personDao: PersonDao,
    private val asistenciaDao: AsistenciaDao,
    private val prefs: PreferencesHelper
) : MaestrosRepository() {

    override fun findCategories(): LiveData<List<Category>> {
        return categoryDao.findCategories()
    }

    override suspend fun findCategoriesBlocking(): List<Category> {
        return categoryDao.findCategoriesBlocking()
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
        return mapToResult {
            appIbartiFaceApi.updateCategory(
                category.id,
                CategoryRequest(category.description)
            )
        }
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

    override suspend fun findStatusesBlocking(): List<Status> {
        return statusDao.findStatusesBlocking()
    }

    override suspend fun insertStatus(statusRequest: StatusRequest): Result<Status> {
        return mapToResult { appIbartiFaceApi.addStatus(statusRequest) }
    }

    override suspend fun updateStatus(status: Status): Result<Status> {
        return mapToResult {
            appIbartiFaceApi.updateStatus(
                status.id,
                StatusRequest(status.category, status.description)
            )
        }
    }

    override suspend fun deleteStatus(idStatus: String): Result<Unit> {
        return mapToResult { appIbartiFaceApi.deleteStatus(idStatus) }
    }

    override suspend fun refreshPersons(): Result<Unit> {
        return mapToResult {
            val persons = appIbartiFaceApi.findPersons()
            personDao.replacePersons(*persons.toTypedArray())
        }
    }

    override fun findPersons(): LiveData<List<Person>> {
        return personDao.findAllPersons()
    }

    override suspend fun insertPerson(addPersonRequest: AddPersonRequest): Result<Unit> {
        return mapToResult {
            appIbartiFaceApi.addPerson(addPersonRequest)
            standByDao.deleteStandBy(addPersonRequest.cliente, addPersonRequest.fecha, addPersonRequest.foto)
        }
    }

    override suspend fun updatePerson(
        personId: String,
        updatePersonRequest: UpdatePersonRequest
    ): Result<Person> {
        return mapToResult { appIbartiFaceApi.updatePerson(personId, updatePersonRequest) }
    }

    override fun findCurrentDayStandBys(): LiveData<List<StandBy>> {
        return standByDao.findStandBysByDate()
    }

    override fun findStandBysByClientAndDate(
        client: String,
        date: String
    ): LiveData<List<StandBy>> {
        return standByDao.findStandBysByClientAndDate(client, date)
    }

    override suspend fun refreshCurrentDayStandBys(force: Boolean): Result<Unit> {
        return mapToResult {
            if(force || prefs.isTimeExpired("${defaultDate()}")){
                val todayStandBys = appIbartiFaceApi.findStandBysCurrentDay()
                prefs.saveTime("${defaultDate()}")
                standByDao.replaceStandBysByDate(currentDay().toString(), *todayStandBys.toTypedArray())
            }
        }
    }

    override suspend fun refreshStandBysByClientAndDate(
        client: String,
        date: String,
        force: Boolean
    ): Result<Unit> {
        debug("refreshStandBysByClientAndDate($client: String, $date: String)")
        return mapToResult {
            if(force || prefs.isTimeExpired("$client$date")){
                val standBys = appIbartiFaceApi.findStandBysByClientAndDate(client, date)
                prefs.saveTime("$client$date")
                standByDao.replaceStandBysByClientAndDate(client, date, *standBys.toTypedArray())
            }
        }
    }

    override suspend fun deleteStandBy(client: String, date: String, url: String): Result<Unit> {
        return mapToResult {
            appIbartiFaceApi.deleteStandBy(
                client = client,
                date = date,
                deleteStandBy = DeleteStandBy(foto = url)
            )
            standByDao.deleteStandBy(client, date, url)
        }
    }

    override fun findAsistencias(iniDate: CustomDate, endDate: CustomDate): LiveData<List<Asistencia>> {
        return asistenciaDao.findAsistencias(iniDate.toString(), endDate.toString())
    }

    override suspend fun refreshAsistencias(iniDate: CustomDate, endDate: CustomDate): Result<Unit> {
        return mapToResult {
            val asistencias = appIbartiFaceApi.findAsistencias(iniDate.toString(), endDate.toString()).map { asis ->
                asis.names = asis.names?.trim()?: "" // the api may return names o surnames as null
                asis.surnames = asis.surnames?.trim()?: ""
                asis
            }
            debug("refreshAsistencias($iniDate: String, $endDate: String)=${asistencias.take(2)}")
            asistenciaDao.replaceAsistencias(iniDate.toString(), endDate.toString(), *asistencias.toTypedArray())
        }
    }

    private suspend fun <T> mapToResult(sth: suspend () -> T): Result<T> {
        return try {
            Result(sth())
        } catch (e: Exception) {
            Result(error = e)
        }
    }
}