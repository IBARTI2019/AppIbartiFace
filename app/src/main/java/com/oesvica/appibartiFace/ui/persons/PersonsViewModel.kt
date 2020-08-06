package com.oesvica.appibartiFace.ui.persons

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.oesvica.appibartiFace.data.model.NetworkRequestStatus
import com.oesvica.appibartiFace.data.model.person.Person
import com.oesvica.appibartiFace.data.repository.MaestrosRepository
import com.oesvica.appibartiFace.utils.base.BaseViewModel
import com.oesvica.appibartiFace.utils.debug
import com.oesvica.appibartiFace.utils.schedulers.SchedulerProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PersonsViewModel
@Inject constructor(
    private val maestrosRepository: MaestrosRepository
) : BaseViewModel() {

    val persons by lazy { maestrosRepository.findPersons() }
    val personsNetworkRequest = MutableLiveData<NetworkRequestStatus>()

    fun refreshPersons() {
        personsNetworkRequest.value = NetworkRequestStatus(isOngoing = true)
        viewModelScope.launch {
            val resultQuery = withContext(IO) { maestrosRepository.refreshPersons() }
            personsNetworkRequest.value =
                NetworkRequestStatus(isOngoing = false, error = resultQuery.error)
        }
    }

    fun deletePerson(person: Person) {
        debug("deletePerson($person: Person)")
        viewModelScope.launch {
            val result = withContext(IO) {maestrosRepository.deletePerson(personId = person.id) }
            debug("deletePerson result = $result")
        }
    }

}
