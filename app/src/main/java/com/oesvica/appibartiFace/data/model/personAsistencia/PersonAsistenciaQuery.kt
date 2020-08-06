package com.oesvica.appibartiFace.data.model.personAsistencia

import android.os.Parcel
import android.os.Parcelable
import com.oesvica.appibartiFace.data.model.CustomDate

data class PersonAsistenciaQuery(var iniDate: CustomDate, val endDate: CustomDate, var isAptos: Boolean) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(CustomDate::class.java.classLoader)!!,
        parcel.readParcelable(CustomDate::class.java.classLoader)!!,
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(iniDate, flags)
        parcel.writeParcelable(endDate, flags)
        parcel.writeByte(if (isAptos) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PersonAsistenciaQuery> {
        override fun createFromParcel(parcel: Parcel): PersonAsistenciaQuery {
            return PersonAsistenciaQuery(parcel)
        }

        override fun newArray(size: Int): Array<PersonAsistenciaQuery?> {
            return arrayOfNulls(size)
        }
    }
}