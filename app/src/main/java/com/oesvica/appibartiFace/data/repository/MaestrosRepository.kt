package com.oesvica.appibartiFace.data.repository

import androidx.lifecycle.LiveData
import com.oesvica.appibartiFace.data.model.*
import com.oesvica.appibartiFace.data.model.asistencia.Asistencia
import com.oesvica.appibartiFace.data.model.auth.AuthInfo
import com.oesvica.appibartiFace.data.model.auth.LogInResponse
import com.oesvica.appibartiFace.data.model.auth.LogOutResponse
import com.oesvica.appibartiFace.data.model.auth.UserData
import com.oesvica.appibartiFace.data.model.category.Category
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
    abstract fun findCategories(): LiveData<List<Category>>
    abstract suspend fun findCategoriesBlocking(): List<Category>
    abstract suspend fun insertCategory(description: String): Result<Category>
    abstract suspend fun updateCategory(category: Category): Result<Category>
    abstract suspend fun deleteCategory(idCategory: String): Result<Unit>

    abstract suspend fun refreshStatuses(): Result<Unit>
    abstract fun findStatuses(): LiveData<List<Status>>
    abstract suspend fun findStatusesBlocking(): List<Status>
    abstract suspend fun insertStatus(statusRequest: StatusRequest): Result<Status>
    abstract suspend fun updateStatus(status: Status): Result<Status>
    abstract suspend fun deleteStatus(idStatus: String): Result<Unit>


    abstract suspend fun refreshPersons(): Result<Unit>
    abstract fun findPersons(): LiveData<List<Person>>
    abstract suspend fun insertPerson(addPersonRequest: AddPersonRequest): Result<Unit>
    abstract suspend fun updatePerson(personId: String, updatePersonRequest: UpdatePersonRequest): Result<Person>


    abstract fun findCurrentDayStandBys(): LiveData<List<StandBy>>
    abstract fun findStandBysByClientAndDate(client: String, date: String): LiveData<List<StandBy>>
    abstract suspend fun refreshCurrentDayStandBys(force: Boolean): Result<Unit>
    abstract suspend fun refreshStandBysByClientAndDate(client: String, date: String, force: Boolean): Result<Unit>
    abstract suspend fun deleteStandBy(client: String, date: String, url: String): Result<Unit>
    
    abstract suspend fun findPredictionsByStandBy(standBy: StandBy): Result<List<Prediction>>

}