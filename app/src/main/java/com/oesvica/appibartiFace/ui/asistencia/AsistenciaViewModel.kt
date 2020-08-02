package com.oesvica.appibartiFace.ui.asistencia

import androidx.lifecycle.*
import com.oesvica.appibartiFace.data.model.asistencia.Asistencia
import com.oesvica.appibartiFace.data.model.CustomDate
import com.oesvica.appibartiFace.data.model.toCustomDate
import com.oesvica.appibartiFace.data.repository.MaestrosRepository
import com.oesvica.appibartiFace.utils.base.BaseViewModel
import com.oesvica.appibartiFace.utils.debug
import com.oesvica.appibartiFace.utils.schedulers.SchedulerProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class AsistenciaFilter(
    var iniDate: CustomDate,
    var endDate: CustomDate
)

class AsistenciaViewModel
@Inject constructor(
    private val maestrosRepository: MaestrosRepository
) : BaseViewModel() {

    private val asistenciasQueryRange: MutableLiveData<AsistenciaFilter> =
        MutableLiveData()

    val asistencias: LiveData<List<Asistencia>> = asistenciasQueryRange.switchMap { filter ->
        maestrosRepository.findAsistencias(
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
        asistenciasQueryRange.value = AsistenciaFilter(iniDate ?: return, endDate ?: return)
        launch {
            val asistenciasResult = withContext(IO) {
                maestrosRepository.refreshAsistencias(iniDate, endDate)
            }
            debug("asistenciasResult = $asistenciasResult")
        }
    }

}