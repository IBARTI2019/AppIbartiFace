package com.oesvica.appibartiFace.ui.persons

import androidx.lifecycle.MutableLiveData
import com.oesvica.appibartiFace.data.model.Person
import com.oesvica.appibartiFace.data.repository.MaestrosRepository
import com.oesvica.appibartiFace.utils.base.BaseViewModel
import com.oesvica.appibartiFace.utils.debug
import com.oesvica.appibartiFace.utils.schedulers.SchedulerProvider
import kotlinx.coroutines.launch
import javax.inject.Inject

class PersonsViewModel
@Inject constructor(
    private val maestrosRepository: MaestrosRepository,
    schedulerProvider: SchedulerProvider
) : BaseViewModel(schedulerProvider) {

    val persons: MutableLiveData<List<Person>> by lazy { MutableLiveData<List<Person>>() }

    fun loadStatuses(){
        launch {
            val resultQuery = maestrosRepository.findPersons()
            if(resultQuery.success != null){
                debug("persons found $resultQuery")
                persons.value = resultQuery.success
            }
            else{
                debug("some error here ${resultQuery.error?.message}")
                resultQuery.error?.printStackTrace()
            }
        }
    }

}
