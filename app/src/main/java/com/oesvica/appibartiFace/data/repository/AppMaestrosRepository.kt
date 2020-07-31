package com.oesvica.appibartiFace.data.repository

import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.oesvica.appibartiFace.data.database.*
import com.oesvica.appibartiFace.data.model.*
import com.oesvica.appibartiFace.data.model.asistencia.Asistencia
import com.oesvica.appibartiFace.data.model.auth.*
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
import com.oesvica.appibartiFace.data.preferences.AppPreferencesHelper.Companion.LOGIN
import com.oesvica.appibartiFace.data.preferences.AppPreferencesHelper.Companion.TOKEN
import com.oesvica.appibartiFace.data.preferences.AppPreferencesHelper.Companion.TOKEN_UPLOADED
import com.oesvica.appibartiFace.data.preferences.AppPreferencesHelper.Companion.USER
import com.oesvica.appibartiFace.data.preferences.PreferencesHelper
import com.oesvica.appibartiFace.data.remote.AppIbartiFaceApi
import com.oesvica.appibartiFace.utils.debug
import com.oesvica.appibartiFace.utils.decoded
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class AppMaestrosRepository
@Inject constructor(
    private val appIbartiFaceApi: AppIbartiFaceApi,
    private val categoryDao: CategoryDao,
    private val statusDao: StatusDao,
    private val standByDao: StandByDao,
    private val personDao: PersonDao,
    private val asistenciaDao: AsistenciaDao,
    private val appDatabase: AppDatabase,
    private val prefs: PreferencesHelper
) : MaestrosRepository() {

    override fun getAuthInfo(): AuthInfo {
        return AuthInfo(logIn = prefs[LOGIN], token = prefs[TOKEN])
    }

    override fun getUserData(): UserData? {
        val token = getAuthInfo().token
        if(token.isNullOrEmpty()) return null
        return Gson().fromJson<UserData>(token.decoded().payload, UserData::class.java)
    }

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

    override suspend fun getFirebaseTokenId(): String? {
        return suspendCoroutine { cont ->
            FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        debug("FirebaseInstanceId getInstance failed ${task.exception}")
                        cont.resume(null)
                        return@OnCompleteListener
                    }
                    val token = task.result?.token
                    debug("FirebaseInstanceId getInstance token=$token")
                    cont.resume(token)
                })
        }
    }

    override suspend fun sendFirebaseTokenId(firebaseTokenId: FirebaseTokenId): Result<FirebaseTokenId> {
        return mapToResult {
            appIbartiFaceApi.sendFirebaseTokenId(
                userId = getUserData()?.id ?: throw Exception("getUserData.id is null"),
                firebaseTokenId = firebaseTokenId
            ).also { prefs["tokenUploaded"] = true }
        }
    }

    override suspend fun logIn(user: String, password: String): Result<LogInResponse> {
        return mapToResult {
            //logOut() didnt work :u
            appIbartiFaceApi.logIn(
                LogInRequest(
                    usuario = user,
                    clave = password
                )
            ).apply {
                prefs[LOGIN] = logIn
                prefs[USER] = user
                prefs[TOKEN] = token
                prefs[TOKEN_UPLOADED] = false
            }
        }
    }

    override suspend fun logOut(): Result<LogOutResponse> {
        return mapToResult {
            appIbartiFaceApi.logOut(LogOutRequest(usuario = prefs[USER]))
                .apply {
                    prefs[LOGIN] = logIn
                    prefs[TOKEN] = ""
                    prefs[TOKEN_UPLOADED] = false
                }.also { appDatabase.clearAllTables() }
        }
    }

    override fun findCategories(): LiveData<List<Category>> {
        return categoryDao.findCategories()
    }

    override suspend fun findCategoriesBlocking(): List<Category> {
        return categoryDao.findCategoriesBlocking()
    }

    override suspend fun refreshCategories(): Result<Unit> {
        return mapToResult {
            val categories = appIbartiFaceApi.findCategories(
                authorization = getAuthInfo().token
            )
            debug("categories=$categories")
            categoryDao.deleteAllCategories()
            categoryDao.insertCategories(*categories.toTypedArray())
        }
    }

    override suspend fun insertCategory(description: String): Result<Category> {
        return mapToResult {
            appIbartiFaceApi.addCategory(
                authorization = getAuthInfo().token,
                categoryRequest = CategoryRequest(
                    description
                )
            )
        }
    }

    override suspend fun updateCategory(category: Category): Result<Category> {
        return mapToResult {
            appIbartiFaceApi.updateCategory(
                authorization = getAuthInfo().token,
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
                authorization = getAuthInfo().token, id = idCategory
            )
        }
    }

    override suspend fun refreshStatuses(): Result<Unit> {
        return mapToResult {
            val statuses = appIbartiFaceApi.findStatuses(
                authorization = getAuthInfo().token
            )
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
        return mapToResult {
            appIbartiFaceApi.addStatus(
                authorization = getAuthInfo().token, statusRequest = statusRequest
            )
        }
    }

    override suspend fun updateStatus(status: Status): Result<Status> {
        return mapToResult {
            appIbartiFaceApi.updateStatus(
                authorization = getAuthInfo().token,
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
                authorization = getAuthInfo().token, id = idStatus
            )
        }
    }

    override suspend fun refreshPersons(): Result<Unit> {
        return mapToResult {
            val persons = appIbartiFaceApi.findPersons(
                authorization = getAuthInfo().token
            )
            personDao.replacePersons(*persons.toTypedArray())
        }
    }

    override fun findPersons(): LiveData<List<Person>> {
        return personDao.findAllPersons()
    }

    override suspend fun insertPerson(addPersonRequest: AddPersonRequest): Result<Unit> {
        return mapToResult {
            appIbartiFaceApi.addPerson(
                authorization = getAuthInfo().token, addPersonRequest = addPersonRequest
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
                authorization = getAuthInfo().token,
                id = personId,
                updatePersonRequest = updatePersonRequest
            )
        }
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
            if (force || prefs.isTimeExpired("${defaultDate()}")) {
                val todayStandBys = appIbartiFaceApi.findStandBysCurrentDay()
                prefs.saveTime("${defaultDate()}")
                standByDao.replaceStandBysByDate(
                    currentDay().toString(),
                    *todayStandBys.toTypedArray()
                )
            }
        }
    }

    override suspend fun refreshStandBysByClientAndDate(
        client: String,
        date: String,
        force: Boolean
    ): Result<Unit> {
        return mapToResult {
            if (force || prefs.isTimeExpired(keyForStandByFetchRequest(client, date))) {
                val standBys = appIbartiFaceApi.findStandBysByClientAndDate(
                    authorization = getAuthInfo().token, client = client, date = date
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
                authorization = getAuthInfo().token,
                client = client,
                date = date,
                deleteStandBy = DeleteStandBy(
                    foto = url
                )
            )
            standByDao.deleteStandBy(client, date, url)
        }
    }

    override suspend fun findPredictionsByStandBy(standBy: StandBy): Result<List<Prediction>> {
        return mapToResult {
            appIbartiFaceApi.findPredictionsByStandBy(
                client = standBy.client,
                date = standBy.date,
                url = standBy.url
            )
        }
    }

    override fun findAsistencias(
        iniDate: CustomDate,
        endDate: CustomDate
    ): LiveData<List<Asistencia>> {
        return asistenciaDao.findAsistencias(iniDate.toString(), endDate.toString())
    }

    override suspend fun refreshAsistencias(
        iniDate: CustomDate,
        endDate: CustomDate
    ): Result<Unit> {
        return mapToResult {
            val asistencias =
                appIbartiFaceApi.findAsistencias(
                    authorization = getAuthInfo().token,
                    iniDate = iniDate.toString(),
                    endDate = endDate.toString()
                )
                    .map { asis ->
                        // the api may return names o surnames as null
                        asis.names = asis.names?.trim() ?: ""
                        asis.surnames = asis.surnames?.trim() ?: ""
                        asis
                    }
            debug("refreshAsistencias($iniDate: String, $endDate: String)=${asistencias.take(2)}")
            asistenciaDao.replaceAsistencias(
                iniDate.toString(),
                endDate.toString(),
                *asistencias.toTypedArray()
            )
        }
    }

    private suspend fun <T> mapToResult(sth: suspend () -> T): Result<T> {
        return try {
            Result(sth())
        } catch (e: Exception) {
            e.printStackTrace()
            Result(error = e)
        }
    }
}