package com.oesvica.appibartiFace.data.model.person

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.oesvica.appibartiFace.data.api.AppIbartiFaceApi
import java.util.*

@Entity(tableName = Person.TABLE_NAME)
data class Person(
    @PrimaryKey @SerializedName("_id") @Expose var id: String = "",
    @SerializedName("category") @Expose var category: String = "",
    @SerializedName("client") @Expose var client: String = "",
    @SerializedName("doc_id") @Expose var doc_id: String = "",
    @SerializedName("status") @Expose var status: String = "",
    @SerializedName("foto") @Expose var photo: String = "",
    @SerializedName("nombres") @Expose var names: String? = "",
    @SerializedName("apellidos") @Expose var surnames: String? = "",
    @SerializedName("fec_nacimiento") @Expose var dateBorn: String = ""
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
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(category)
        parcel.writeString(client)
        parcel.writeString(doc_id)
        parcel.writeString(status)
        parcel.writeString(photo)
        parcel.writeString(names)
        parcel.writeString(surnames)
        parcel.writeString(dateBorn)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Person> {
        const val TABLE_NAME = "Person"
        override fun createFromParcel(parcel: Parcel): Person {
            return Person(parcel)
        }

        override fun newArray(size: Int): Array<Person?> {
            return arrayOfNulls(size)
        }
    }

}

fun Person.fullName(): String = (names?.split(" ")?.get(0)?.toLowerCase(Locale.getDefault())?.capitalize() ?: "") + " " + (surnames?.split(" ")?.get(0)?.toLowerCase(Locale.getDefault())?.capitalize() ?: "")

fun Person.fullPhotoUrl(): String = "${AppIbartiFaceApi.END_POINT}view${photo.trimStart('.')}"