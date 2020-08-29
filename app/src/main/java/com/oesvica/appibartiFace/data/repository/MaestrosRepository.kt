package com.oesvica.appibartiFace.data.repository

import androidx.lifecycle.LiveData
import com.oesvica.appibartiFace.data.model.*
import com.oesvica.appibartiFace.data.model.category.Category
import com.oesvica.appibartiFace.data.model.location.Location
import com.oesvica.appibartiFace.data.model.person.AddPersonRequest
import com.oesvica.appibartiFace.data.model.person.Person
import com.oesvica.appibartiFace.data.model.person.UpdatePersonRequest
import com.oesvica.appibartiFace.data.model.standby.Prediction
import com.oesvica.appibartiFace.data.model.standby.StandBy
import com.oesvica.appibartiFace.data.model.status.Status
import com.oesvica.appibartiFace.data.model.status.StatusRequest

abstract class MaestrosRepository {

    abstract fun addClient(client: String)
    abstract fun getClients(): List<String>

    abstract suspend fun refreshCategories(): Result<Unit>
    abstract fun loadCategories(): LiveData<List<Category>>
    abstract suspend fun findCategoriesSynchronous(): List<Category>
    abstract suspend fun insertCategory(description: String): Result<Category>
    abstract suspend fun updateCategory(category: Category): Result<Category>
    abstract suspend fun deleteCategory(idCategory: String): Result<Unit>

    abstract suspend fun refreshStatuses(): Result<List<Status>>
    abstract fun loadStatuses(): LiveData<List<Status>>
    abstract suspend fun findStatusesSynchronous(): List<Status>
    abstract suspend fun insertStatus(statusRequest: StatusRequest): Result<Status>
    abstract suspend fun updateStatus(status: Status): Result<Status>
    abstract suspend fun deleteStatus(idStatus: String): Result<Unit>

    abstract suspend fun refreshLocations(): Result<List<Location>>
    abstract suspend fun findLocationsSynchronous(): List<Location>


    abstract suspend fun refreshPersons(): Result<Unit>
    abstract fun loadPersons(): LiveData<List<Person>>
    abstract suspend fun insertPerson(addPersonRequest: AddPersonRequest): Result<Unit>
    abstract suspend fun updatePerson(personId: String, updatePersonRequest: UpdatePersonRequest): Result<Person>
    abstract suspend fun deletePerson(personId: String): Result<Unit>



    abstract fun loadStandBys(client: String, date: String): LiveData<List<StandBy>>
    abstract suspend fun refreshStandBys(client: String, date: String, force: Boolean): Result<Unit>
    abstract suspend fun deleteStandBy(client: String, date: String, url: String): Result<Unit>
    
    abstract suspend fun fetchPredictionsByStandBy(standBy: StandBy): Result<List<Prediction>>

}