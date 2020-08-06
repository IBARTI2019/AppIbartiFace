package com.oesvica.appibartiFace.ui.personAsistencia

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.oesvica.appibartiFace.data.model.NetworkRequestStatus
import com.oesvica.appibartiFace.data.model.personAsistencia.PersonAsistenciaQuery
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

    private val personAsistenciaQuery: MutableLiveData<PersonAsistenciaQuery> by lazy { MutableLiveData<PersonAsistenciaQuery>() }

    val personAsistencias by lazy {
        personAsistenciaQuery.switchMap { filter ->
            reportsRepository.findPersonAsistencias(
                iniDate = filter.iniDate,
                endDate = filter.endDate,
                isApto = filter.isAptos
            )
        }
    }
    val fetchPersonAsistenciasNetworkRequest = MutableLiveData<NetworkRequestStatus>()

    fun searchPersonAsistencias(query: PersonAsistenciaQuery, force: Boolean = false) {
        if (fetchPersonAsistenciasNetworkRequest.value?.isOngoing == true && !force) {
            // Avoid making double requests on device rotation
            return
        }
        fetchPersonAsistenciasNetworkRequest.value = NetworkRequestStatus(isOngoing = true)
        personAsistenciaQuery.value = query
        viewModelScope.launch {
            val result = withContext(IO) {
                reportsRepository.refreshPersonAsistencias(
                    query.iniDate,
                    query.endDate,
                    query.isAptos
                )
            }
            fetchPersonAsistenciasNetworkRequest.value =
                NetworkRequestStatus(isOngoing = false, error = result.error)
            debug(
                "searchPersonAsistencias($query: PersonAsistenciaQuery)=${result.success?.take(2)}"
            )
        }
    }

}