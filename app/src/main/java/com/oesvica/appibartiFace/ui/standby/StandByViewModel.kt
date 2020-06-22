package com.oesvica.appibartiFace.ui.standby

import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.*
import com.oesvica.appibartiFace.data.model.*
import com.oesvica.appibartiFace.data.repository.MaestrosRepository
import com.oesvica.appibartiFace.utils.base.BaseViewModel
import com.oesvica.appibartiFace.utils.debug
import com.oesvica.appibartiFace.utils.schedulers.SchedulerProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class StandByQuery(var client: String, var date: CustomDate) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()?:"",
        parcel.readParcelable(CustomDate::class.java.classLoader)!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(client)
        parcel.writeParcelable(date, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StandByQuery> {
        override fun createFromParcel(parcel: Parcel): StandByQuery {
            return StandByQuery(parcel)
        }

        override fun newArray(size: Int): Array<StandByQuery?> {
            return arrayOfNulls(size)
        }
    }
}

class StandByViewModel @Inject constructor(
    private val maestrosRepository: MaestrosRepository,
    schedulerProvider: SchedulerProvider
) : BaseViewModel(schedulerProvider) {


    private val standBysQuery: MutableLiveData<StandByQuery> = MutableLiveData()
    var standBys: LiveData<List<StandBy>> = standBysQuery.switchMap {
        if(it.client.trim().isEmpty()) { // if no client is pass search default today standbys
            maestrosRepository.findCurrentDayStandBys()
        }
        else { // otherwise search standbys by client and date
            maestrosRepository.findStandBysByClientAndDate(client = it.client, date = it.date.toString())
        }
    }
    val fetchStandBysNetworkRequest = MutableLiveData<NetworkRequestStatus>()

    fun loadTodayStandBys() {
        debug("loading today standbys")
        standBysQuery.value = StandByQuery("", currentDay())
        fetchStandBysNetworkRequest.value = NetworkRequestStatus(isOngoing = true)
        launch {
            val standbys = withContext(IO) { maestrosRepository.refreshCurrentDayStandBys() }
            debug("loadTodayStandBys result standBys=$standbys")
            fetchStandBysNetworkRequest.value = NetworkRequestStatus(isOngoing = false, error = standbys.error)
        }
    }

    fun searchStandBys(query: StandByQuery) {
        debug("searchStandBys(${query.client}: String, ${query.date}: String)")
        fetchStandBysNetworkRequest.value = NetworkRequestStatus(isOngoing = true)
        standBysQuery.value = query
        launch {
            val standsBysResult = withContext(IO) {
                maestrosRepository.refreshStandBysByClientAndDate(query.client, query.date.toString())
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