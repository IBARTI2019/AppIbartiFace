package com.oesvica.appibartiFace.ui.asistencia

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.oesvica.appibartiFace.data.model.Asistencia
import com.oesvica.appibartiFace.data.model.CustomDate
import com.oesvica.appibartiFace.data.model.currentDay
import com.oesvica.appibartiFace.data.model.toCustomDate
import com.oesvica.appibartiFace.data.repository.MaestrosRepository
import com.oesvica.appibartiFace.utils.base.BaseViewModel
import com.oesvica.appibartiFace.utils.debug
import com.oesvica.appibartiFace.utils.schedulers.SchedulerProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AsistenciaViewModel
@Inject constructor(
    private val maestrosRepository: MaestrosRepository,
    schedulerProvider: SchedulerProvider
) : BaseViewModel(schedulerProvider) {

    private val asistenciasQueryRange: MutableLiveData<Pair<CustomDate, CustomDate>> =
        MutableLiveData()

    val asistencias: LiveData<List<Asistencia>> =
        Transformations.map(Transformations.switchMap(asistenciasQueryRange) {
            maestrosRepository.findAsistencias(
                iniDate = it.first,
                endDate = it.second
            )
        }) { list ->
            list.sortedWith(compareBy({ it.date.toCustomDate() }, { it.time }))
        }

    fun refreshAsistencias(iniDate: CustomDate?, endDate: CustomDate?) {
        asistenciasQueryRange.value = (iniDate ?: return) to (endDate ?: return)
        launch {
            val asistenciasResult = withContext(IO) {
                maestrosRepository.refreshAsistencias(iniDate, endDate)
            }
            debug("asistenciasResult = $asistenciasResult")
        }
    }

}