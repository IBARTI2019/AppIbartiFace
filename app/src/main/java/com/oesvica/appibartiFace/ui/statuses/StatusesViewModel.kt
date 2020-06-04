package com.oesvica.appibartiFace.ui.statuses

import androidx.lifecycle.MutableLiveData
import com.oesvica.appibartiFace.data.model.Person
import com.oesvica.appibartiFace.data.model.Status
import com.oesvica.appibartiFace.data.repository.MaestrosRepository
import com.oesvica.appibartiFace.utils.base.BaseViewModel
import com.oesvica.appibartiFace.utils.debug
import com.oesvica.appibartiFace.utils.schedulers.SchedulerProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StatusesViewModel
@Inject constructor(
    private val maestrosRepository: MaestrosRepository,
    schedulerProvider: SchedulerProvider
) : BaseViewModel(schedulerProvider) {

    val statuses by lazy { maestrosRepository.findStatuses() }

    fun loadStatuses(){
        debug("loadStatuses")
        launch {
            withContext(IO){
                maestrosRepository.refreshStatuses()
            }
            /*
            val resultQuery = maestrosRepository.findStatuses()
            if(resultQuery.success != null){
                debug("statuses found $resultQuery")
                statuses.value = resultQuery.success
            }
            else{
                debug("some error here ${resultQuery.error?.message}")
                resultQuery.error?.printStackTrace()
            }*/
        }
    }

    fun deleteStatus(status: Status){
        launch {
            withContext(IO){
                maestrosRepository.deleteStatus(status.id)
            }
            loadStatuses()
        }
    }

}
