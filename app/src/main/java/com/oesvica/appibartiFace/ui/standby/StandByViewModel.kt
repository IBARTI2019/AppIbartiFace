package com.oesvica.appibartiFace.ui.standby

import androidx.lifecycle.MediatorLiveData
import com.oesvica.appibartiFace.data.model.StandBy
import com.oesvica.appibartiFace.data.repository.MaestrosRepository
import com.oesvica.appibartiFace.utils.base.BaseViewModel
import com.oesvica.appibartiFace.utils.debug
import com.oesvica.appibartiFace.utils.schedulers.SchedulerProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StandByViewModel @Inject constructor(
    private val maestrosRepository: MaestrosRepository,
    schedulerProvider: SchedulerProvider
) : BaseViewModel(schedulerProvider) {

    private val todayStandBys by lazy { maestrosRepository.findCurrentDayStandBys() }

    var standBys = MediatorLiveData<List<StandBy>>()
    private var todayStandBysLoaded = false

    fun loadTodayStandBys() {
        debug("loading today standbys")
        if(!todayStandBysLoaded){
            standBys.addSource(todayStandBys) {
                standBys.value = it
            }
        }
        todayStandBysLoaded = true
        launch {
            val standbys = withContext(IO) { maestrosRepository.refreshCurrentDayStandBys() }
            debug("loadTodayStandBys result standBys=$standbys")
        }
    }

    fun searchStandBys(client: String, date: String, onFinished: () -> Unit) {
        debug("searchStandBys($client: String, $date: String)")
        launch {
            val standsBysResult = withContext(IO) {
                maestrosRepository.refreshStandBysByClientAndDate(client, date)
            }
            debug("searchStandBys standsBysResult=$standsBysResult")
            onFinished()
            standBys.removeSource(todayStandBys)
            standBys.addSource(maestrosRepository.findStandBysByClientAndDate(client, date)) {
                standBys.value = it
            }
        }
    }

    fun deleteStandBy(standBy: StandBy) {
        launch {
            val resultDelete = withContext(IO) {
                maestrosRepository.deleteStandBy(standBy.client, standBy.date, standBy.url)
            }
            debug("resultDelete=$resultDelete")
            loadTodayStandBys()
        }
    }

}