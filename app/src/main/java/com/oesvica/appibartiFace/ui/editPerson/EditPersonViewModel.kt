package com.oesvica.appibartiFace.ui.editPerson

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

    fun editPerson(
        idPerson: String, idCategory: String, idStatus: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        debug("editPerson($idPerson: String, $idCategory: String, $idStatus: String)")
        launch {
            val result = withContext(IO){
                maestrosRepository.updatePerson(idPerson, UpdatePersonRequest(category = idCategory, status = idStatus))
            }
            debug("result editPerson=$result")
            if (result.success != null) {
                onSuccess()
            } else {
                onError()
            }
        }

    }

}