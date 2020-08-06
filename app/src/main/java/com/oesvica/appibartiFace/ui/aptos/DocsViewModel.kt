package com.oesvica.appibartiFace.ui.aptos

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.oesvica.appibartiFace.data.api.Doc
import com.oesvica.appibartiFace.data.model.CustomDate
import com.oesvica.appibartiFace.data.model.toCustomDate
import com.oesvica.appibartiFace.data.repository.ReportsRepository
import com.oesvica.appibartiFace.utils.base.BaseViewModel
import com.oesvica.appibartiFace.utils.debug
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DocsViewModel
@Inject constructor(
    private val reportsRepository: ReportsRepository
) : BaseViewModel() {

    val docs: MutableLiveData<List<Doc>> by lazy { MutableLiveData<List<Doc>>() }

    fun refreshDocs(aptos: Boolean, iniDate: CustomDate, endDate: CustomDate) {
        viewModelScope.launch {
            val result = withContext(IO) {
                if (aptos) reportsRepository.refreshAptos(iniDate, endDate)
                else reportsRepository.refreshNoAptos(iniDate, endDate)
            }
            debug("refreshDocs($aptos: Boolean, $iniDate: CustomDate, $endDate: CustomDate)=${result.success?.take(2)}")
            if (result.success != null){
                docs.value = result.success.sortedWith(compareBy({ it.date.toCustomDate() }, { it.time })).reversed()
            }
        }
    }

}