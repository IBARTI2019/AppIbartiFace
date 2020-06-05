package com.oesvica.appibartiFace.ui.statuses

import androidx.lifecycle.MutableLiveData
import com.oesvica.appibartiFace.data.model.NetworkRequestStatus
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
    val networkRequestStatuses = MutableLiveData<NetworkRequestStatus>()

    fun refreshStatuses(){
        networkRequestStatuses.value = NetworkRequestStatus(isOngoing = true)
        launch {
            val result = withContext(IO){
                maestrosRepository.refreshStatuses()
            }
            networkRequestStatuses.value = NetworkRequestStatus(isOngoing = false, error = result.error)
        }
    }

    fun deleteStatus(status: Status){
        launch {
            withContext(IO){ maestrosRepository.deleteStatus(status.id) }
            refreshStatuses()
        }
    }

}
