package com.oesvica.appibartiFace.ui.addStatus

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.oesvica.appibartiFace.data.model.NetworkRequestStatus
import com.oesvica.appibartiFace.data.model.status.Status
import com.oesvica.appibartiFace.data.model.status.StatusRequest
import com.oesvica.appibartiFace.data.repository.MaestrosRepository
import com.oesvica.appibartiFace.utils.base.BaseViewModel
import com.oesvica.appibartiFace.utils.coroutines.CoroutineContextProvider
import com.oesvica.appibartiFace.utils.debug
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddStatusViewModel
@Inject constructor(
    private val maestrosRepository: MaestrosRepository,
    coroutineContextProvider: CoroutineContextProvider
) : BaseViewModel(coroutineContextProvider) {

    val categories by lazy { maestrosRepository.loadCategories() }
    val addStatusNetworkRequest = MutableLiveData<NetworkRequestStatus>()

    fun addStatus(
        statusId: String? = null,
        categoryId: String,
        description: String
    ) {
        debug("addStatus $statusId  $categoryId  $description")
        addStatusNetworkRequest.value = NetworkRequestStatus(isOngoing = true)
        viewModelScope.launch {
            val response = withContext(IO){
                if (statusId == null) maestrosRepository.insertStatus(
                    StatusRequest(
                        categoryId,
                        description
                    )
                )
                else maestrosRepository.updateStatus(
                    Status(
                        id = statusId,
                        category = categoryId,
                        description = description
                    )
                )
            }
            debug("response addStatus=$response")
            addStatusNetworkRequest.value = NetworkRequestStatus(isOngoing = false, error = response.error)
        }
    }

}