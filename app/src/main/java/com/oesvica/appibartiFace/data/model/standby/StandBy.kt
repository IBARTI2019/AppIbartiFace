package com.oesvica.appibartiFace.data.model.standby

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.oesvica.appibartiFace.data.remote.AppIbartiFaceApi

@Entity(tableName = StandBy.TABLE_NAME)
data class StandBy(
    @SerializedName("cliente") @Expose var client: String = "",
    @SerializedName("dispositivo") @Expose var device: String = "",
    @SerializedName("fecha") @Expose val date: String = "",
    @SerializedName("hora") @Expose var time: String = "",
    @PrimaryKey @SerializedName("url") @Expose var url: String = ""
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(client)
        parcel.writeString(device)
        parcel.writeString(date)
        parcel.writeString(time)
        parcel.writeString(url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StandBy> {
        const val TABLE_NAME = "StandBy"
        override fun createFromParcel(parcel: Parcel): StandBy {
            return StandBy(parcel)
        }

        override fun newArray(size: Int): Array<StandBy?> {
            return arrayOfNulls(size)
        }
    }
}

fun StandBy.properUrl(): String = "${AppIbartiFaceApi.END_POINT}${AppIbartiFaceApi.STAND_BY}$client/$date/$url"