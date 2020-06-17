package com.oesvica.appibartiFace.ui.addPerson

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.oesvica.appibartiFace.data.model.AddPersonRequest
import com.oesvica.appibartiFace.data.model.Category
import com.oesvica.appibartiFace.data.model.NetworkRequestStatus
import com.oesvica.appibartiFace.data.model.Status
import com.oesvica.appibartiFace.data.repository.MaestrosRepository
import com.oesvica.appibartiFace.utils.base.BaseViewModel
import com.oesvica.appibartiFace.utils.debug
import com.oesvica.appibartiFace.utils.schedulers.SchedulerProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddPersonViewModel
@Inject constructor(
    private val maestrosRepository: MaestrosRepository,
    schedulerProvider: SchedulerProvider
) : BaseViewModel(schedulerProvider) {

    val categories = liveData {
        val list = maestrosRepository.findCategoriesBlocking()
        if (list.isEmpty()) {
            val result = withContext(IO) {
                maestrosRepository.refreshCategories()
            }
            if (result.success != null) {
                emit(maestrosRepository.findCategoriesBlocking())
            } else {
                emit(emptyList<Category>())
            }
        } else {
            emit(list)
        }
    }
    val statuses = liveData {
        val list = maestrosRepository.findStatusesBlocking()
        if (list.isEmpty()) {
            val result = withContext(IO) {
                maestrosRepository.refreshStatuses()
            }
            if (result.success != null) {
                emit(maestrosRepository.findStatusesBlocking())
            } else {
                emit(emptyList<Status>())
            }
        } else {
            emit(list)
        }
    }

    val addPersonNetworkRequest = MutableLiveData<NetworkRequestStatus>()

    fun addPerson(
        cedula: String,
        category: String,
        status: String,
        client: String,
        device: String,
        date: String,
        photo: String
    ) {
        debug(
            "addPerson(\n" +
                    "        $cedula: String,\n" +
                    "        $category: String,\n" +
                    "        $status: String,\n" +
                    "        $client: String,\n" +
                    "        $device: String,\n" +
                    "        $date: String,\n" +
                    "        $photo: String"
        )
        addPersonNetworkRequest.value = NetworkRequestStatus(isOngoing = true)
        launch {
            val result = withContext(IO) {
                maestrosRepository.insertPerson(
                    AddPersonRequest(
                        cedula = cedula,
                        category = category,
                        status = status,
                        cliente = client,
                        dispositivo = device,
                        fecha = date,
                        foto = photo
                    )
                )
            }
            debug("result add person=$result")
            addPersonNetworkRequest.value =
                NetworkRequestStatus(isOngoing = false, error = result.error)
        }
    }

}