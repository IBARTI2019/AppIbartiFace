package com.oesvica.appibartiFace.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = Person.TABLE_NAME)
data class Person(
    @PrimaryKey @SerializedName("_id") @Expose var id: String = "",
    @SerializedName("category") @Expose var category: String = "",
    @SerializedName("client") @Expose var client: String = "",
    @SerializedName("doc_id") @Expose var doc_id: String = "",
    @SerializedName("status") @Expose var status: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
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