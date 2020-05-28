package com.oesvica.appibartiFace.ui.categories

import androidx.lifecycle.MutableLiveData
import com.oesvica.appibartiFace.data.model.Category
import com.oesvica.appibartiFace.data.repository.CategoriesRepository
import com.oesvica.appibartiFace.utils.base.BaseViewModel
import com.oesvica.appibartiFace.utils.debug
import com.oesvica.appibartiFace.utils.schedulers.SchedulerProvider
import javax.inject.Inject
import kotlin.math.log

class CategoriesViewModel
@Inject constructor(
    private val categoriesRepository: CategoriesRepository,
    schedulerProvider: SchedulerProvider
) : BaseViewModel(schedulerProvider) {

    val categories: MutableLiveData<List<Category>> by lazy { MutableLiveData<List<Category>>() }


    fun loadCategories(){
        debug("loading categories")

        add(categoriesRepository.findCategories()
            .applySchedulers()
            .distinctUntilChanged()
            .subscribe( {
                debug("categories found $it")
            }, {

            }))
    }

}
