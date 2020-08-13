package com.oesvica.appibartiFace.ui.statuses

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.oesvica.appibartiFace.data.model.NetworkRequestStatus
import com.oesvica.appibartiFace.data.model.status.Status
import com.oesvica.appibartiFace.data.repository.MaestrosRepository
import com.oesvica.appibartiFace.utils.base.BaseViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StatusesViewModel
@Inject constructor(
    private val maestrosRepository: MaestrosRepository
) : BaseViewModel() {

    val statuses by lazy { maestrosRepository.loadStatuses() }
    val networkRequestStatuses = MutableLiveData<NetworkRequestStatus>()

    fun refreshStatuses(){
        networkRequestStatuses.value = NetworkRequestStatus(isOngoing = true)
        viewModelScope.launch {
            val result = withContext(IO){
                maestrosRepository.refreshStatuses()
            }
            networkRequestStatuses.value = NetworkRequestStatus(isOngoing = false, error = result.error)
        }
    }

    fun deleteStatus(status: Status){
        viewModelScope.launch {
            withContext(IO){ maestrosRepository.deleteStatus(status.id) }
            refreshStatuses()
        }
    }

}
