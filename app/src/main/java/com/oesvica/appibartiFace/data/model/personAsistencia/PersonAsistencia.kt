package com.oesvica.appibartiFace.data.model.personAsistencia

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import com.oesvica.appibartiFace.data.api.AppIbartiFaceApi
import java.util.*

@Entity(tableName = PersonAsistencia.TABLE_NAME, primaryKeys = ["cedula", "dateEntry", "timeEntry"])
data class PersonAsistencia(
    @SerializedName("cedula")
    var cedula: String = "",
    @SerializedName("nombres")
    var names: String? = "",
    @SerializedName("apellidos")
    var surnames: String? = "",
    @SerializedName("fecha_entrada")
    var dateEntry: String = "",
    @SerializedName("hora_entrada")
    var timeEntry: String = "",
    @SerializedName("foto_entrada")
    var photoEntry: String = "",
    var isApto: Boolean? = null
//    @SerializedName("documentos")
//    var docs: List<Doc> = emptyList()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(cedula)
        parcel.writeString(names)
        parcel.writeString(surnames)
        parcel.writeString(dateEntry)
        parcel.writeString(timeEntry)
        parcel.writeString(photoEntry)
        parcel.writeByte(if (isApto == true) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PersonAsistencia> {
        const val TABLE_NAME = "PersonAsistencia"
        override fun createFromParcel(parcel: Parcel): PersonAsistencia {
            return PersonAsistencia(parcel)
        }

        override fun newArray(size: Int): Array<PersonAsistencia?> {
            return arrayOfNulls(size)
        }
    }

}

fun PersonAsistencia.fullPhotoUrl(): String = "${AppIbartiFaceApi.END_POINT}view$photoEntry"
fun PersonAsistencia.fullName(): String =
    (names?.split(" ")?.get(0)?.toLowerCase(Locale.getDefault())?.capitalize()
        ?: "") + " " + (surnames?.split(" ")?.get(0)?.toLowerCase(
        Locale.getDefault()
    )?.capitalize() ?: "")

