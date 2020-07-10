package com.oesvica.appibartiFace.data.model.standby

import android.os.Parcel
import android.os.Parcelable
import com.oesvica.appibartiFace.data.model.CustomDate

data class StandByQuery(var client: String, var date: CustomDate) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
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
            return StandByQuery(
                parcel
            )
        }

        override fun newArray(size: Int): Array<StandByQuery?> {
            return arrayOfNulls(size)
        }
    }
}