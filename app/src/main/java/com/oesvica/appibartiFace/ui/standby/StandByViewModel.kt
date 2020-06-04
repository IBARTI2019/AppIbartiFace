package com.oesvica.appibartiFace.ui.standby

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

    val standBys by lazy { maestrosRepository.findCurrentDayStandBys() }

    fun loadTodayStandBys() {
        debug("loading today standbys")
        launch {
            val standbys = withContext(IO) { maestrosRepository.refreshCurrentDayStandBys() }
            debug("result standBys=$standbys")
        }
    }

    fun deleteStandBy(standBy: StandBy){
        launch {
            val resultDelete = withContext(IO){
                maestrosRepository.deleteStandBy(standBy.client, standBy.date, standBy.url)
            }
            debug("resultDelete=$resultDelete")
            loadTodayStandBys()
        }
    }

}