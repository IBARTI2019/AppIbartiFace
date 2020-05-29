package com.oesvica.appibartiFace.data.remote

import com.oesvica.appibartiFace.data.model.Category
import com.oesvica.appibartiFace.data.model.Person
import com.oesvica.appibartiFace.data.model.Status
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Url

interface AppIbartiFaceApi {

    companion object {
        const val END_POINT = "http://oesvica.ddns.net:5003/"
        const val FIND_CATEGORIES = "category"
        const val FIND_STATUSES = "status"
        const val FIND_PERSONS = "persons"
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
}