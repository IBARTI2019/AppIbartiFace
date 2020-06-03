package com.oesvica.appibartiFace.data.remote

import com.oesvica.appibartiFace.data.model.Category
import com.oesvica.appibartiFace.data.model.CategoryRequest
import com.oesvica.appibartiFace.data.model.Person
import com.oesvica.appibartiFace.data.model.Status
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.Deferred
import retrofit2.http.*

interface AppIbartiFaceApi {

    companion object {
        const val END_POINT = "http://oesvica.ddns.net:5003/"
        const val FIND_CATEGORIES = "category"
        const val FIND_STATUSES = "status"
        const val FIND_PERSONS = "persons"

        const val ADD_CATEGORY = "category/"
        const val UPDATE_CATEGORY = "category/{id}/"
        const val DELETE_CATEGORY = "category/{id}/"
    }

    @GET
    suspend fun findCategories(
        @Url url: String = FIND_CATEGORIES
    ): List<Category>

    @GET
    suspend fun findStatuses(
        @Url url: String = FIND_STATUSES
    ): List<Status>

    @GET
    suspend fun findPersons(
        @Url url: String = FIND_PERSONS
    ): List<Person>

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
    ): Unit
}