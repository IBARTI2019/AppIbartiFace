package com.oesvica.appibartiFace

import android.os.Parcel
import android.os.Parcelable
import com.oesvica.appibartiFace.data.model.standby.StandBy

fun createCopyFomParcel0(parcelable: Parcelable): Parcelable {
    val parcel = Parcel.obtain()
    parcelable.writeToParcel(parcel, 0)
    parcel.setDataPosition(0)
    return StandBy.createFromParcel(parcel)
}

fun Parcelable.getParcel(): Parcel {
    val parcel = Parcel.obtain()
    this.writeToParcel(parcel, 0)
    parcel.setDataPosition(0)
    return parcel
}

fun <P: Parcelable> createCopyFomParcel2(parcelable: P): Parcelable {
    val parcel = Parcel.obtain()
    parcelable.writeToParcel(parcel, 0)
    parcel.setDataPosition(0)
    val cl = parcelable::class
    val c = cl.constructors
    val g = StandBy(parcel)
    return StandBy.createFromParcel(parcel)
}