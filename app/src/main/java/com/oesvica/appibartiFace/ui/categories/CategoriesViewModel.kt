package com.oesvica.appibartiFace.ui.categories

import androidx.lifecycle.MutableLiveData
import com.oesvica.appibartiFace.data.model.Category
import com.oesvica.appibartiFace.data.repository.MaestrosRepository
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

    fun loadCategories(){
        debug("loading categories")
        launch {
            val categoriesFetched = maestrosRepository.findCategories()
            debug("categories found $categoriesFetched")
            categories.value = categoriesFetched
        }
    }

}
