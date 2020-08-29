package com.oesvica.appibartiFace.ui.standby

import CachedResourceList
import androidx.lifecycle.*
import com.oesvica.appibartiFace.data.model.*
import com.oesvica.appibartiFace.data.model.location.Location
import com.oesvica.appibartiFace.data.model.standby.StandBy
import com.oesvica.appibartiFace.data.model.standby.StandByQuery
import com.oesvica.appibartiFace.data.repository.MaestrosRepository
import com.oesvica.appibartiFace.utils.base.BaseViewModel
import com.oesvica.appibartiFace.utils.debug
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StandByViewModel @Inject constructor(
    private val maestrosRepository: MaestrosRepository
) : BaseViewModel() {

    val locations by lazy {
        object : CachedResourceList<Location>() {
            override suspend fun loadFromDb(): List<Location> =
                maestrosRepository.findLocationsSynchronous()
            override suspend fun fetch(): Result<List<Location>> =
                maestrosRepository.refreshLocations()
        }.asLiveData(IO)
    }

//    val locationsTraditionalWay = liveData {
//        val list = maestrosRepository.findLocationsSynchronous()
//        if (list.isNullOrEmpty()) {
//            val result = withContext(IO) {
//                maestrosRepository.refreshLocations()
//            }
//            if (result.success != null) {
//                emit(result.success)
//            } else {
//                emit(emptyList())
//            }
//        } else {
//            emit(list)
//        }
//    }

    private val standBysQuery: MutableLiveData<StandByQuery> = MutableLiveData()
    var standBys: LiveData<List<StandBy>> = standBysQuery.switchMap { filter ->
        maestrosRepository.loadStandBys(
            client = filter.client,
            date = filter.date.toString()
        ).map { list ->
            list.sortedWith(compareBy({ it.date.toCustomDate() }, { it.time })).reversed()
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
            viewModelScope.launch {
                val standsBysResult = withContext(IO) {
                    maestrosRepository.refreshStandBys(
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
        viewModelScope.launch {
            val resultDelete = withContext(IO) {
                maestrosRepository.deleteStandBy(standBy.client, standBy.date, standBy.url)
            }
            debug("resultDelete=$resultDelete")
        }
    }

}