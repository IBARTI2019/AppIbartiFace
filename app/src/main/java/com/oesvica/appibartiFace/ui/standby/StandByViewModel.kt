package com.oesvica.appibartiFace.ui.standby

import androidx.lifecycle.*
import com.oesvica.appibartiFace.data.model.*
import com.oesvica.appibartiFace.data.model.standby.StandBy
import com.oesvica.appibartiFace.data.model.standby.StandByQuery
import com.oesvica.appibartiFace.data.repository.MaestrosRepository
import com.oesvica.appibartiFace.utils.base.BaseViewModel
import com.oesvica.appibartiFace.utils.debug
import com.oesvica.appibartiFace.utils.schedulers.SchedulerProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StandByViewModel @Inject constructor(
    private val maestrosRepository: MaestrosRepository
) : BaseViewModel() {

    private val standBysQuery: MutableLiveData<StandByQuery> = MutableLiveData()
    var standBys: LiveData<List<StandBy>> = standBysQuery.switchMap { filter ->
        maestrosRepository.findStandBysByClientAndDate(
            client = filter.client,
            date = filter.date.toString()
        ).map { list ->
            list.map { standBy -> standBy.apply { time = time.take(8) } }
                .sortedWith(compareBy({ it.date.toCustomDate() }, { it.time })).reversed()
        }
    }
    val fetchStandBysNetworkRequest = MutableLiveData<NetworkRequestStatus>()

    fun getClients() = maestrosRepository.getClients()

    fun searchStandBys(query: StandByQuery, force: Boolean = false) {
        maestrosRepository.addClient(query.client)
        // Verify that there is not an ongoing fetchStandBy request as not to make double api calls on device rotation
        if (fetchStandBysNetworkRequest.value?.isOngoing == false || force) {
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
                fetchStandBysNetworkRequest.value =
                    NetworkRequestStatus(isOngoing = false, error = standsBysResult.error)
            }
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