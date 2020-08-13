package com.oesvica.appibartiFace.data.repository

import androidx.lifecycle.LiveData
import com.oesvica.appibartiFace.data.database.*
import com.oesvica.appibartiFace.data.model.*
import com.oesvica.appibartiFace.data.model.category.Category
import com.oesvica.appibartiFace.data.model.category.CategoryRequest
import com.oesvica.appibartiFace.data.model.person.AddPersonRequest
import com.oesvica.appibartiFace.data.model.person.Person
import com.oesvica.appibartiFace.data.model.person.UpdatePersonRequest
import com.oesvica.appibartiFace.data.model.standby.DeleteStandBy
import com.oesvica.appibartiFace.data.model.standby.Prediction
import com.oesvica.appibartiFace.data.model.standby.StandBy
import com.oesvica.appibartiFace.data.model.status.Status
import com.oesvica.appibartiFace.data.model.status.StatusRequest
import com.oesvica.appibartiFace.data.preferences.AppPreferencesHelper.Companion.CLIENTS
import com.oesvica.appibartiFace.data.preferences.AppPreferencesHelper.Companion.TOKEN
import com.oesvica.appibartiFace.data.preferences.PreferencesHelper
import com.oesvica.appibartiFace.data.api.AppIbartiFaceApi
import com.oesvica.appibartiFace.utils.debug
import com.oesvica.appibartiFace.utils.mapToResult
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
    private val prefs: PreferencesHelper
) : MaestrosRepository() {

    override fun addClient(client: String) {
        val clients = prefs.get<Set<String>>(CLIENTS)
        if (!clients.contains(client.trim())) {
            prefs[CLIENTS] = clients.toMutableSet().apply { add(client.trim()) }
        }
    }

    override fun getClients(): List<String> {
        val clients = prefs.get<Set<String>>(CLIENTS)
        return clients.toList()
    }

    override fun loadCategories(): LiveData<List<Category>> {
        return categoryDao.findCategories()
    }

    override suspend fun findCategoriesSynchronous(): List<Category> {
        return categoryDao.findCategoriesSynchronous()
    }

    override suspend fun refreshCategories(): Result<Unit> {
        return mapToResult {
            val categories = appIbartiFaceApi.findCategories(
                authorization = prefs[TOKEN]
            )
            debug("categories=$categories")
            categoryDao.replaceCategories(*categories.toTypedArray())
        }
    }

    override suspend fun insertCategory(description: String): Result<Category> {
        return mapToResult {
            appIbartiFaceApi.addCategory(
                authorization = prefs[TOKEN],
                categoryRequest = CategoryRequest(
                    description
                )
            )
        }
    }

    override suspend fun updateCategory(category: Category): Result<Category> {
        return mapToResult {
            appIbartiFaceApi.updateCategory(
                authorization = prefs[TOKEN],
                id = category.id,
                categoryRequest = CategoryRequest(
                    category.description
                )
            )
        }
    }

    override suspend fun deleteCategory(idCategory: String): Result<Unit> {
        return mapToResult {
            appIbartiFaceApi.deleteCategory(
                authorization = prefs[TOKEN], id = idCategory
            )
        }
    }

    override suspend fun refreshStatuses(): Result<List<Status>> {
        return mapToResult {
            val statuses = appIbartiFaceApi.findStatuses(
                authorization = prefs[TOKEN]
            )
            debug("statuses=$statuses")
            statusDao.replaceStatuses(*statuses.toTypedArray())
            statuses
        }
    }

    override fun loadStatuses(): LiveData<List<Status>> {
        return statusDao.findStatuses()
    }

    override suspend fun findStatusesSynchronous(): List<Status> {
        return statusDao.findStatusesSynchronous()
    }

    override suspend fun insertStatus(statusRequest: StatusRequest): Result<Status> {
        return mapToResult {
            appIbartiFaceApi.addStatus(
                authorization = prefs[TOKEN], statusRequest = statusRequest
            )
        }
    }

    override suspend fun updateStatus(status: Status): Result<Status> {
        return mapToResult {
            appIbartiFaceApi.updateStatus(
                authorization = prefs[TOKEN],
                id = status.id,
                statusRequest = StatusRequest(
                    status.category,
                    status.description
                )
            )
        }
    }

    override suspend fun deleteStatus(idStatus: String): Result<Unit> {
        return mapToResult {
            appIbartiFaceApi.deleteStatus(
                authorization = prefs[TOKEN], id = idStatus
            )
        }
    }

    override suspend fun refreshPersons(): Result<Unit> {
        return mapToResult {
            val persons = appIbartiFaceApi.findPersons(
                authorization = prefs[TOKEN]
            ).map { person ->
                // the api may return names o surnames as null
                person.names = person.names?.trim() ?: ""
                person.surnames = person.surnames?.trim() ?: ""
                person
            }
            personDao.replacePersons(*persons.toTypedArray())
        }
    }

    override fun loadPersons(): LiveData<List<Person>> {
        return personDao.findAllPersons()
    }

    override suspend fun insertPerson(addPersonRequest: AddPersonRequest): Result<Unit> {
        return mapToResult {
            appIbartiFaceApi.addPerson(
                authorization = prefs[TOKEN], addPersonRequest = addPersonRequest
            )
            standByDao.deleteStandBy(
                addPersonRequest.cliente,
                addPersonRequest.fecha,
                addPersonRequest.foto
            )
        }
    }

    override suspend fun updatePerson(
        personId: String,
        updatePersonRequest: UpdatePersonRequest
    ): Result<Person> {
        return mapToResult {
            appIbartiFaceApi.updatePerson(
                authorization = prefs[TOKEN],
                id = personId,
                updatePersonRequest = updatePersonRequest
            )
        }
    }

    override suspend fun deletePerson(personId: String): Result<Unit> {
        return mapToResult {
            appIbartiFaceApi.deletePerson(
                authorization = prefs[TOKEN],
                id = personId
            )
            personDao.deletePerson(personId)
        }
    }

    override fun loadStandBys(
        client: String,
        date: String
    ): LiveData<List<StandBy>> {
        return standByDao.findStandBysByClientAndDate(client, date)
    }

    override suspend fun refreshStandBys(
        client: String,
        date: String,
        force: Boolean
    ): Result<Unit> {
        return mapToResult {
            if (force || prefs.isTimeExpired(keyForStandByFetchRequest(client, date))) {
                val standBys = appIbartiFaceApi.findStandBysByClientAndDate(
                    authorization = prefs[TOKEN], client = client, date = date
                )
                prefs.saveTime(keyForStandByFetchRequest(client, date))
                standByDao.replaceStandBysByClientAndDate(client, date, *standBys.toTypedArray())
            }
        }
    }

    private fun keyForStandByFetchRequest(client: String, date: String) = "$client$date"

    override suspend fun deleteStandBy(client: String, date: String, url: String): Result<Unit> {
        return mapToResult {
            appIbartiFaceApi.deleteStandBy(
                authorization = prefs[TOKEN],
                client = client,
                date = date,
                deleteStandBy = DeleteStandBy(
                    foto = url
                )
            )
            standByDao.deleteStandBy(client, date, url)
        }
    }

    override suspend fun fetchPredictionsByStandBy(standBy: StandBy): Result<List<Prediction>> {
        return mapToResult {
            appIbartiFaceApi.findPredictionsByStandBy(
                client = standBy.client,
                date = standBy.date,
                url = standBy.url
            )
        }
    }
}