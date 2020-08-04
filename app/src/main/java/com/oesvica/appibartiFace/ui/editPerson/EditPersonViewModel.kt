package com.oesvica.appibartiFace.ui.editPerson

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.oesvica.appibartiFace.data.model.category.Category
import com.oesvica.appibartiFace.data.model.NetworkRequestStatus
import com.oesvica.appibartiFace.data.model.status.Status
import com.oesvica.appibartiFace.data.model.person.UpdatePersonRequest
import com.oesvica.appibartiFace.data.repository.MaestrosRepository
import com.oesvica.appibartiFace.utils.base.BaseViewModel
import com.oesvica.appibartiFace.utils.debug
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EditPersonViewModel
@Inject constructor(
    private val maestrosRepository: MaestrosRepository
) : BaseViewModel() {

    val categories: LiveData<List<Category>> = liveData {
        val list = maestrosRepository.findCategoriesSynchronous()
        if (list.isEmpty()) {
            val result = withContext(IO) {
                maestrosRepository.refreshCategories()
            }
            if (result.success != null) {
                emit(maestrosRepository.findCategoriesSynchronous())
            } else {
                emit(emptyList())
            }
        } else {
            emit(list)
        }
    }

    val statuses: LiveData<List<Status>> = liveData {
        val list = maestrosRepository.findStatusesSynchronous()
        if (list.isEmpty()) {
            val result = withContext(IO) {
                maestrosRepository.refreshStatuses()
            }
            if (result.success != null) {
                emit(maestrosRepository.findStatusesSynchronous())
            } else {
                emit(emptyList())
            }
        } else {
            emit(list)
        }
    }

    val editPersonNetworkRequest = MutableLiveData<NetworkRequestStatus>()

    fun editPerson(
        idPerson: String, idCategory: String, idStatus: String
    ) {
        editPersonNetworkRequest.value = NetworkRequestStatus(isOngoing = true)
        debug("editPerson($idPerson: String, $idCategory: String, $idStatus: String)")
        launch {
            val result = withContext(IO) {
                maestrosRepository.updatePerson(
                    idPerson,
                    UpdatePersonRequest(
                        category = idCategory,
                        status = idStatus
                    )
                )
            }
            debug("result editPerson=$result")
            editPersonNetworkRequest.value =
                NetworkRequestStatus(isOngoing = false, error = result.error)
        }

    }

}