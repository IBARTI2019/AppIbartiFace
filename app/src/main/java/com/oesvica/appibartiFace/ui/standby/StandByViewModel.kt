package com.oesvica.appibartiFace.ui.standby

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.oesvica.appibartiFace.data.model.NetworkRequestStatus
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


    private val standBysQuery: MutableLiveData<Pair<String, String>> = MutableLiveData()
    var standBys: LiveData<List<StandBy>> = Transformations.switchMap(standBysQuery) {
        if(it.first.trim().isEmpty()) { // if no client is pass search default today standbys
            maestrosRepository.findCurrentDayStandBys()
        }
        else { // otherwise search standbys by client and date
            maestrosRepository.findStandBysByClientAndDate(client = it.first, date = it.second)
        }
    }
    val fetchStandBysNetworkRequest = MutableLiveData<NetworkRequestStatus>()

    fun loadTodayStandBys() {
        debug("loading today standbys")
        standBysQuery.value = "" to ""
        fetchStandBysNetworkRequest.value = NetworkRequestStatus(isOngoing = true)
        launch {
            val standbys = withContext(IO) { maestrosRepository.refreshCurrentDayStandBys() }
            debug("loadTodayStandBys result standBys=$standbys")
            fetchStandBysNetworkRequest.value = NetworkRequestStatus(isOngoing = false, error = standbys.error)
        }
    }

    fun searchStandBys(client: String, date: String) {
        debug("searchStandBys($client: String, $date: String)")
        fetchStandBysNetworkRequest.value = NetworkRequestStatus(isOngoing = true)
        standBysQuery.value = client to date
        launch {
            val standsBysResult = withContext(IO) {
                maestrosRepository.refreshStandBysByClientAndDate(client, date)
            }
            debug("searchStandBys standsBysResult=$standsBysResult")
            fetchStandBysNetworkRequest.value = NetworkRequestStatus(isOngoing = false, error = standsBysResult.error)
        }
    }

    fun deleteStandBy(standBy: StandBy) {
        launch {
            val resultDelete = withContext(IO) {
                maestrosRepository.deleteStandBy(standBy.client, standBy.date, standBy.url)
            }
            debug("resultDelete=$resultDelete")
        }
    }

}