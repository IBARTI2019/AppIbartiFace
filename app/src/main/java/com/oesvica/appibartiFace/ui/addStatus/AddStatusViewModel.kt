package com.oesvica.appibartiFace.ui.addStatus

import com.oesvica.appibartiFace.data.model.Status
import com.oesvica.appibartiFace.data.model.StatusRequest
import com.oesvica.appibartiFace.data.repository.MaestrosRepository
import com.oesvica.appibartiFace.utils.base.BaseViewModel
import com.oesvica.appibartiFace.utils.debug
import com.oesvica.appibartiFace.utils.schedulers.SchedulerProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddStatusViewModel
@Inject constructor(
    private val maestrosRepository: MaestrosRepository,
    schedulerProvider: SchedulerProvider
) : BaseViewModel(schedulerProvider) {

    val categories by lazy { maestrosRepository.findCategories() }

    fun addStatus(
        statusId: String? = null,
        categoryId: String,
        description: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        debug("addStatus $statusId  $categoryId  $description")
        launch {
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
            if (response.success != null) onSuccess()
            else onError()
        }
    }

}