package com.oesvica.appibartiFace.data.remote

import com.oesvica.appibartiFace.data.model.FirebaseTokenId
import com.oesvica.appibartiFace.data.model.asistencia.Asistencia
import com.oesvica.appibartiFace.data.model.auth.LogInRequest
import com.oesvica.appibartiFace.data.model.auth.LogInResponse
import com.oesvica.appibartiFace.data.model.auth.LogOutRequest
import com.oesvica.appibartiFace.data.model.auth.LogOutResponse
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
import retrofit2.http.*

interface AppIbartiFaceApi {

    companion object {
        const val END_POINT = "http://oesvica.ddns.net:5005/"

        const val FIND_PERSONS = "maestros/persons"
        const val ADD_PERSON = "maestros/persons/"
        const val UPDATE_PERSON = "maestros/persons/{id}/"

        const val FIND_CATEGORIES = "maestros/category"
        const val ADD_CATEGORY = "maestros/category/"
        const val UPDATE_CATEGORY = "maestros/category/{id}/"
        const val DELETE_CATEGORY = "maestros/category/{id}/"

        const val FIND_STATUSES = "maestros/status"
        const val ADD_STATUS = "maestros/status"
        const val UPDATE_STATUS = "maestros/status/{id}/"
        const val DELETE_STATUS = "maestros/status/{id}/"

        const val STAND_BY = "standby/fotos/"
        const val STAND_BY_BY_DATE = "standby/fotos/{client}/{date}"
        const val STAND_BY_PREDICT = "standby/standbyPredict/{client}/{date}/{url}"
        const val DELETE_STAND_BY = "standby/fotos/{client}/{date}/"

        const val ASISTENCIAS = "reporte/asistencia-ibarti/{iniDate}/{endDate}/"
        const val APTOS = "reporte/asistencia-apto/{iniDate}/{endDate}/"
        const val NO_APTOS = "reporte/asistencia-noapto/{iniDate}/{endDate}/"

        const val SEND_FIREBASE_TOKEN_ID = "fireUser/{userId}"

        const val LOG_IN = "usuario/login/"
        const val LOG_OUT = "usuario/logout/"

        fun imgUrlForStandBy(client: String, date: String, url: String) = "$END_POINT/view/standby/$client/$date/$url"
    }

    @PUT(SEND_FIREBASE_TOKEN_ID)
    suspend fun sendFirebaseTokenId(
        @Path("userId") userId: String,
        @Body firebaseTokenId: FirebaseTokenId
    ): FirebaseTokenId

    @POST(LOG_IN)
    suspend fun logIn(
        @Body logInRequest: LogInRequest
    ): LogInResponse

    @POST(LOG_OUT)
    suspend fun logOut(
        @Body logOutRequest: LogOutRequest
    ): LogOutResponse

    @GET
    suspend fun findPersons(
        @Header("Authorization") authorization: String?,
        @Url url: String = FIND_PERSONS
    ): List<Person>

    @POST(ADD_PERSON)
    suspend fun addPerson(
        @Header("Authorization") authorization: String?,
        @Body addPersonRequest: AddPersonRequest
    ): Person

    @PUT(UPDATE_PERSON)
    suspend fun updatePerson(
        @Header("Authorization") authorization: String?,
        @Path("id") id: String,
        @Body updatePersonRequest: UpdatePersonRequest
    ): Person

    @GET
    suspend fun findCategories(
        @Header("Authorization") authorization: String?,
        @Url url: String = FIND_CATEGORIES
    ): List<Category>

    @POST(ADD_CATEGORY)
    suspend fun addCategory(
        @Header("Authorization") authorization: String?,
        @Body categoryRequest: CategoryRequest
    ): Category

    @PUT(UPDATE_CATEGORY)
    suspend fun updateCategory(
        @Header("Authorization") authorization: String?,
        @Path("id") id: String,
        @Body categoryRequest: CategoryRequest
    ): Category

    @DELETE(DELETE_CATEGORY)
    suspend fun deleteCategory(
        @Header("Authorization") authorization: String?,
        @Path("id") id: String
    )


    @GET
    suspend fun findStatuses(
        @Header("Authorization") authorization: String?,
        @Url url: String = FIND_STATUSES
    ): List<Status>

    @POST(ADD_STATUS)
    suspend fun addStatus(
        @Header("Authorization") authorization: String?,
        @Body statusRequest: StatusRequest
    ): Status

    @PUT(UPDATE_STATUS)
    suspend fun updateStatus(
        @Header("Authorization") authorization: String?,
        @Path("id") id: String,
        @Body statusRequest: StatusRequest
    ): Status

    @DELETE(DELETE_STATUS)
    suspend fun deleteStatus(
        @Header("Authorization") authorization: String?,
        @Path("id") id: String
    )

    @GET(STAND_BY)
    suspend fun findStandBysCurrentDay(): List<StandBy>

    @GET(STAND_BY_BY_DATE)
    suspend fun findStandBysByClientAndDate(
        @Header("Authorization") authorization: String?,
        @Path("client") client: String,
        @Path("date") date: String
    ): List<StandBy>

    @GET(STAND_BY_PREDICT)
    suspend fun findPredictionsByStandBy(
        @Path("client") client: String,
        @Path("date") date: String,
        @Path("url") url: String
    ): List<Prediction>

    @HTTP(method = "DELETE", path = DELETE_STAND_BY, hasBody = true)
    suspend fun deleteStandBy(
        @Header("Authorization") authorization: String?,
        @Path("client") client: String,
        @Path("date") date: String,
        @Body deleteStandBy: DeleteStandBy
    )

    @GET(ASISTENCIAS)
    suspend fun findAsistencias(
        @Header("Authorization") authorization: String?,
        @Path("iniDate") iniDate: String,
        @Path("endDate") endDate: String
    ): List<Asistencia>

}
