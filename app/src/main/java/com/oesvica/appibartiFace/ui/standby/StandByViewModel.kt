package com.oesvica.appibartiFace.ui.standby

import androidx.lifecycle.*
import com.oesvica.appibartiFace.data.model.*
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


    private val standBysQuery: MutableLiveData<StandByQuery> = MutableLiveData()
    var standBys: LiveData<List<StandBy>> = standBysQuery.switchMap { filter ->
        (if (filter.client.trim().isEmpty()) { // if no client is pass search default today standbys
            maestrosRepository.findCurrentDayStandBys()
        } else { // otherwise search standbys by client and date
            maestrosRepository.findStandBysByClientAndDate(
                client = filter.client,
                date = filter.date.toString()
            )
        }).map { list ->
            list.map { standBy -> standBy.apply { time = time.take(8) } }
                .sortedWith(compareBy({ it.date.toCustomDate() }, { it.time })).reversed()
        }
    }
    val fetchStandBysNetworkRequest = MutableLiveData<NetworkRequestStatus>()

    fun loadTodayStandBys(force: Boolean = false) {
        standBysQuery.value = StandByQuery("", currentDay())
        fetchStandBysNetworkRequest.value = NetworkRequestStatus(isOngoing = true)
        launch {
            val standbys = withContext(IO) {
                maestrosRepository.refreshCurrentDayStandBys(force)
            }
            debug("loadTodayStandBys result standBys=$standbys")
            fetchStandBysNetworkRequest.value =
                NetworkRequestStatus(isOngoing = false, error = standbys.error)
        }
    }

    fun searchStandBys(query: StandByQuery, force: Boolean = false) {
        fetchStandBysNetworkRequest.value = NetworkRequestStatus(isOngoing = true)
        standBysQuery.value = query
        launch {
            val standsBysResult = withContext(IO) {
                maestrosRepository.refreshStandBysByClientAndDate(
                    query.client,
                    query.date.toString(),
                    force
                )
            }
            debug("searchStandBys standsBysResult=$standsBysResult")
            fetchStandBysNetworkRequest.value =
                NetworkRequestStatus(isOngoing = false, error = standsBysResult.error)
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