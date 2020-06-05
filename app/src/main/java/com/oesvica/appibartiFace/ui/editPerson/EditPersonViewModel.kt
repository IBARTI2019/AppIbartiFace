package com.oesvica.appibartiFace.ui.editPerson

import androidx.lifecycle.MutableLiveData
import com.oesvica.appibartiFace.data.model.NetworkRequestStatus
import com.oesvica.appibartiFace.data.model.UpdatePersonRequest
import com.oesvica.appibartiFace.data.repository.MaestrosRepository
import com.oesvica.appibartiFace.utils.base.BaseViewModel
import com.oesvica.appibartiFace.utils.debug
import com.oesvica.appibartiFace.utils.schedulers.SchedulerProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EditPersonViewModel
@Inject constructor(
    private val maestrosRepository: MaestrosRepository,
    schedulerProvider: SchedulerProvider
) : BaseViewModel(schedulerProvider) {

    val categories by lazy { maestrosRepository.findCategories() }
    val statuses by lazy { maestrosRepository.findStatuses() }
    val editPersonNetworkRequest = MutableLiveData<NetworkRequestStatus>()

    fun editPerson(
        idPerson: String, idCategory: String, idStatus: String
    ) {
        editPersonNetworkRequest.value = NetworkRequestStatus(isOngoing = true)
        debug("editPerson($idPerson: String, $idCategory: String, $idStatus: String)")
        launch {
            val result = withContext(IO){
                maestrosRepository.updatePerson(idPerson, UpdatePersonRequest(category = idCategory, status = idStatus))
            }
            debug("result editPerson=$result")
            editPersonNetworkRequest.value = NetworkRequestStatus(isOngoing = false, error = result.error)
        }

    }

}