package com.oesvica.appibartiFace.data.remote

import com.oesvica.appibartiFace.data.model.*
import retrofit2.http.*

interface AppIbartiFaceApi {

    companion object {
        const val END_POINT = "http://oesvica.ddns.net:5003/"

        const val FIND_PERSONS = "persons"
        const val ADD_PERSON = "insert-person"
        const val UPDATE_PERSON = "persons/{id}/"

        const val FIND_CATEGORIES = "category"
        const val ADD_CATEGORY = "category/"
        const val UPDATE_CATEGORY = "category/{id}/"
        const val DELETE_CATEGORY = "category/{id}/"

        const val FIND_STATUSES = "status"
        const val ADD_STATUS = "status/"
        const val UPDATE_STATUS = "status/{id}/"
        const val DELETE_STATUS = "status/{id}/"

        const val STAND_BY = "standby/"
        const val STAND_BY_BY_DATE = "standby/{client}/{date}"
        const val DELETE_STAND_BY = "delete-standby/{client}/{date}"

        const val ASISTENCIAS = "reporte/asistencia-ibarti/{iniDate}/{endDate}/"

        fun imgUrlForStandBy(client: String, date: String, url: String) = "$END_POINT$STAND_BY$client/$date/$url"
    }

    @GET
    suspend fun findPersons(
        @Url url: String = FIND_PERSONS
    ): List<Person>

    @POST(ADD_PERSON)
    suspend fun addPerson(
        @Body addPersonRequest: AddPersonRequest
    ): Person

    @PUT(UPDATE_PERSON)
    suspend fun updatePerson(
        @Path("id") id: String,
        @Body updatePersonRequest: UpdatePersonRequest
    ): Person

    @GET
    suspend fun findCategories(
        @Url url: String = FIND_CATEGORIES
    ): List<Category>

    @POST(ADD_CATEGORY)
    suspend fun addCategory(
        @Body categoryRequest: CategoryRequest
    ): Category

    @PUT(UPDATE_CATEGORY)
    suspend fun updateCategory(
        @Path("id") id: String,
        @Body categoryRequest: CategoryRequest
    ): Category

    @DELETE(DELETE_CATEGORY)
    suspend fun deleteCategory(
        @Path("id") id: String
    )


    @GET
    suspend fun findStatuses(
        @Url url: String = FIND_STATUSES
    ): List<Status>

    @POST(ADD_STATUS)
    suspend fun addStatus(
        @Body statusRequest: StatusRequest
    ): Status

    @PUT(UPDATE_STATUS)
    suspend fun updateStatus(
        @Path("id") id: String,
        @Body statusRequest: StatusRequest
    ): Status

    @DELETE(DELETE_STATUS)
    suspend fun deleteStatus(
        @Path("id") id: String
    )

    @GET(STAND_BY)
    suspend fun findStandBysCurrentDay(): List<StandBy>

    @GET(STAND_BY_BY_DATE)
    suspend fun findStandBysByClientAndDate(
        @Path("client") client: String,
        @Path("date") date: String
    ): List<StandBy>

    @POST(DELETE_STAND_BY)
    suspend fun deleteStandBy(
        @Path("client") client: String,
        @Path("date") date: String,
        @Body deleteStandBy: DeleteStandBy
    )

    @GET(ASISTENCIAS)
    suspend fun findAsistencias(
        @Path("iniDate") iniDate: String,
        @Path("endDate") endDate: String
    ): List<Asistencia>

}
