package com.oesvica.appibartiFace.ui.asistencia

import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.PRIVATE
import androidx.lifecycle.*
import com.oesvica.appibartiFace.data.model.CustomDate
import com.oesvica.appibartiFace.data.model.asistencia.Asistencia
import com.oesvica.appibartiFace.data.model.asistencia.AsistenciaFilter
import com.oesvica.appibartiFace.data.model.toCustomDate
import com.oesvica.appibartiFace.data.repository.ReportsRepository
import com.oesvica.appibartiFace.utils.base.BaseViewModel
import com.oesvica.appibartiFace.utils.coroutines.CoroutineContextProvider
import com.oesvica.appibartiFace.utils.debug
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AsistenciaViewModel
@Inject constructor(
    private val reportsRepository: ReportsRepository,
    coroutineContextProvider: CoroutineContextProvider
) : BaseViewModel(coroutineContextProvider) {

    @VisibleForTesting(otherwise = PRIVATE)
    val asistenciasQueryRange: MutableLiveData<AsistenciaFilter> =
        MutableLiveData()

    val asistencias: LiveData<List<Asistencia>> = asistenciasQueryRange.switchMap { filter ->
        reportsRepository.findAsistencias(
            iniDate = filter.iniDate,
            endDate = filter.endDate
        ).map { list ->
            list.sortedWith(compareBy({ it.date.toCustomDate() }, { it.time }))
        }
    }

    fun refreshAsistencias(
        iniDate: CustomDate?,
        endDate: CustomDate?
    ) {
        debug("refreshAsistencia")
        asistenciasQueryRange.value =
            AsistenciaFilter(
                iniDate ?: return,
                endDate ?: return
            )
        viewModelScope.launch {
            val asistenciasResult = withContext(IO) {
                reportsRepository.refreshAsistencias(iniDate, endDate)
            }
            debug("asistenciasResult = $asistenciasResult")
        }
    }

}