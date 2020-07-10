package com.oesvica.appibartiFace.data.model.asistencia

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = Asistencia.TABLE_NAME, primaryKeys = ["docId", "code"])
data class Asistencia(
    @SerializedName("apellidos") @Expose var surnames: String? = "",
    @SerializedName("cedula") @Expose var docId: String = "",
    @SerializedName("cliente") @Expose var client: String = "",
    @SerializedName("cod_cliente") @Expose var codClient: String = "",
    @SerializedName("cod_dispositivo") @Expose var codDevice: String = "",
    @SerializedName("cod_ficha") @Expose var codFicha: String = "",
    @SerializedName("cod_ubicacion") @Expose var codLocation: String = "",
    @SerializedName("codigo") @Expose var code: String = "",
    @SerializedName("fecha") @Expose var date: String = "",
    @SerializedName("fechaserver") @Expose var serverDate: String = "",
    @SerializedName("hora") @Expose var time: String = "",
    @SerializedName("nombres") @Expose var names: String? = "",
    @SerializedName("ubicacion") @Expose var location: String = "",
    @SerializedName("vetado") @Expose var vetado: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(surnames)
        parcel.writeString(docId)
        parcel.writeString(client)
        parcel.writeString(codClient)
        parcel.writeString(codDevice)
        parcel.writeString(codFicha)
        parcel.writeString(codLocation)
        parcel.writeString(code)
        parcel.writeString(date)
        parcel.writeString(serverDate)
        parcel.writeString(time)
        parcel.writeString(names)
        parcel.writeString(location)
        parcel.writeString(vetado)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Asistencia> {
        const val TABLE_NAME = "Asistencia"

        override fun createFromParcel(parcel: Parcel): Asistencia {
            return Asistencia(
                parcel
            )
        }

        override fun newArray(size: Int): Array<Asistencia?> {
            return arrayOfNulls(size)
        }
    }
}