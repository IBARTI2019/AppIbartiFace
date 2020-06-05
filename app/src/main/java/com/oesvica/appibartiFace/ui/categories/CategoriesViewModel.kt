package com.oesvica.appibartiFace.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.oesvica.appibartiFace.data.model.Category
import com.oesvica.appibartiFace.data.model.NetworkRequestStatus
import com.oesvica.appibartiFace.data.model.Result
import com.oesvica.appibartiFace.data.repository.MaestrosRepository
import com.oesvica.appibartiFace.utils.SingleLiveEvent
import com.oesvica.appibartiFace.utils.base.BaseViewModel
import com.oesvica.appibartiFace.utils.debug
import com.oesvica.appibartiFace.utils.schedulers.SchedulerProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Error
import javax.inject.Inject

class CategoriesViewModel
@Inject constructor(
    private val maestrosRepository: MaestrosRepository,
    schedulerProvider: SchedulerProvider
) : BaseViewModel(schedulerProvider) {

    val categories by lazy { maestrosRepository.findCategories() }
    val statusCategories = MutableLiveData<NetworkRequestStatus>()
    private val _snackBarMsg = SingleLiveEvent<String>()
    val snackBarMsg: LiveData<String>
        get() = _snackBarMsg

    fun refreshCategories() {
        debug("refreshCategories")
        statusCategories.value = NetworkRequestStatus(isOngoing = true)
        launch {
            val resultQuery = withContext(IO) { maestrosRepository.refreshCategories() }
            debug("resultQuery=$resultQuery")
            statusCategories.value =
                NetworkRequestStatus(isOngoing = false, error = resultQuery.error)
        }
    }

    fun addCategory(description: String) {
        debug("addCategory $description")
        _snackBarMsg.value = "Agregando categoria"
        launch {
            val result = withContext(IO) { maestrosRepository.insertCategory(description) }
            debug("addCategory result $result")
            if (result.success != null) {
                _snackBarMsg.postValue("Categoria agregada exitosamente")
            } else {
                _snackBarMsg.postValue("Hubo un error agregando la categoria")
            }
            refreshCategories()
        }
    }

    fun updateCategory(category: Category) {
        debug("updating category $category")
        _snackBarMsg.value = "Actualizando categoria"
        launch {
            val result = withContext(IO) { maestrosRepository.updateCategory(category) }
            debug("updateCategory result $result")
            if (result.success != null) {
                _snackBarMsg.postValue("Categoria actualizada exitosamente")
            } else {
                _snackBarMsg.postValue("Hubo un error actualizando la categoria")
            }
            refreshCategories()
        }
    }

    fun deleteCategory(idCategory: String) {
        debug("deleteCategory $idCategory")
        _snackBarMsg.value = "Eliminando categoria"
        launch {
            val result = withContext(IO) { maestrosRepository.deleteCategory(idCategory) }
            debug("deleteCategory result $result")
            if (result.success != null) {
                _snackBarMsg.postValue("Categoria eliminada exitosamente")
            } else {
                _snackBarMsg.postValue("Hubo un error eliminando la categoria")
            }
            refreshCategories()
        }
    }

}
