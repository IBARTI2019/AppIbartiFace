package com.oesvica.appibartiFace.ui.categories

import androidx.lifecycle.MutableLiveData
import com.oesvica.appibartiFace.data.model.category.Category
import com.oesvica.appibartiFace.data.model.NetworkRequestStatus
import com.oesvica.appibartiFace.data.repository.MaestrosRepository
import com.oesvica.appibartiFace.utils.SingleLiveEvent
import com.oesvica.appibartiFace.utils.base.BaseViewModel
import com.oesvica.appibartiFace.utils.debug
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CategoriesViewModel
@Inject constructor(
    private val maestrosRepository: MaestrosRepository
) : BaseViewModel() {

    val categories by lazy { maestrosRepository.findCategories() }
    val statusCategories = MutableLiveData<NetworkRequestStatus>()
    val snackBarMsg = SingleLiveEvent<String>()

    fun refreshCategories() {
        debug("refreshCategories")
        statusCategories.value = NetworkRequestStatus(isOngoing = true)
//        viewModelScope.launch {
//
//        }
        launch {
            val resultQuery = withContext(IO) { maestrosRepository.refreshCategories() }
            debug("resultQuery=$resultQuery")
            statusCategories.value =
                NetworkRequestStatus(isOngoing = false, error = resultQuery.error)
        }
    }

    fun addCategory(description: String) {
        debug("addCategory $description")
        snackBarMsg.value = "Agregando categoria"
        launch {
            val result = withContext(IO) { maestrosRepository.insertCategory(description) }
            debug("addCategory result $result")
            if (result.success != null) {
                snackBarMsg.value = "Categoria agregada exitosamente"
            } else {
                snackBarMsg.value = "Hubo un error agregando la categoria"
            }
            refreshCategories()
        }
    }

    fun updateCategory(category: Category) {
        debug("updating category $category")
        snackBarMsg.value = "Actualizando categoria"
        launch {
            val result = withContext(IO) { maestrosRepository.updateCategory(category) }
            debug("updateCategory result $result")
            if (result.success != null) {
                snackBarMsg.postValue("Categoria actualizada exitosamente")
            } else {
                snackBarMsg.postValue("Hubo un error actualizando la categoria")
            }
            refreshCategories()
        }
    }

    fun deleteCategory(idCategory: String) {
        debug("deleteCategory $idCategory")
        snackBarMsg.value = "Eliminando categoria"
        launch {
            val result = withContext(IO) { maestrosRepository.deleteCategory(idCategory) }
            debug("deleteCategory result $result")
            if (result.success != null) {
                snackBarMsg.postValue("Categoria eliminada exitosamente")
            } else {
                snackBarMsg.postValue("Hubo un error eliminando la categoria")
            }
            refreshCategories()
        }
    }

}
