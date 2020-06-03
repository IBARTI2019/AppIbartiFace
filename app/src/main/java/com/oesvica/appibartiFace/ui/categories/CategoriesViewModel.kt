package com.oesvica.appibartiFace.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.oesvica.appibartiFace.data.model.Category
import com.oesvica.appibartiFace.data.repository.MaestrosRepository
import com.oesvica.appibartiFace.utils.SingleLiveEvent
import com.oesvica.appibartiFace.utils.base.BaseViewModel
import com.oesvica.appibartiFace.utils.debug
import com.oesvica.appibartiFace.utils.schedulers.SchedulerProvider
import kotlinx.coroutines.launch
import javax.inject.Inject

class CategoriesViewModel
@Inject constructor(
    private val maestrosRepository: MaestrosRepository,
    schedulerProvider: SchedulerProvider
) : BaseViewModel(schedulerProvider) {

    val categories: MutableLiveData<List<Category>> by lazy { MutableLiveData<List<Category>>() }
    private val _snackBarMsg = SingleLiveEvent<String>()
    val snackBarMsg: LiveData<String>
        get() = _snackBarMsg

    fun loadCategories() {
        debug("loading categories")
        launch {
            val resultQuery = maestrosRepository.findCategories()
            if (resultQuery.success != null) {
                debug("categories found $resultQuery")
                categories.value = resultQuery.success
            } else {
                debug("some error here ${resultQuery.error?.message}")
                resultQuery.error?.printStackTrace()
            }
        }
    }

    fun addCategory(description: String) {
        debug("addCategory $description")
        _snackBarMsg.value = "Agregando categoria"
        launch {
            maestrosRepository.insertCategory(description).let {
                debug("add result $it")
                if (it.success != null) {
                    _snackBarMsg.value = "Categoria agregada exitosamente"
                } else {
                    _snackBarMsg.value = "Hubo un error agregando la categoria"
                }
            }
        }
    }

    fun updateCategory(category: Category) {
        debug("updating category $category")
        _snackBarMsg.value = "Actualizando categoria"
        launch {
            maestrosRepository.updateCategory(category).let {
                debug("update result $it")
                if (it.success != null) {
                    _snackBarMsg.value = "Categoria actualizada exitosamente"
                } else {
                    _snackBarMsg.value = "Hubo un error actualizando la categoria"
                }
            }
        }
    }

    fun deleteCategory(idCategory: String){
        debug("deleteCategory $idCategory")
        _snackBarMsg.value = "Eliminando categoria"
        launch {
            maestrosRepository.deleteCategory(idCategory).let {
                debug("delete result $it")
                if (it.success != null) {
                    _snackBarMsg.value = "Categoria eliminada exitosamente"
                } else {
                    _snackBarMsg.value = "Hubo un error eliminando la categoria"
                }
            }
        }
    }

}
