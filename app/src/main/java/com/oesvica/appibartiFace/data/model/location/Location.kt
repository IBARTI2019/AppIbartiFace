package com.oesvica.appibartiFace.data.model.location

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.oesvica.appibartiFace.data.model.location.Location.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class Location(
    @PrimaryKey @SerializedName("_id") @Expose var id: String = "",
    @SerializedName("codigo_sucursal") @Expose var codeSucursal: String = "", // cliente
    @SerializedName("descripcion") @Expose var description: String = "",
    @SerializedName("direction") @Expose var address: String = "",
    @Ignore @SerializedName("dispositivos") @Expose var devices: List<String> = emptyList(),
    @SerializedName("email") @Expose var email: String = "",
    @SerializedName("observation") @Expose var observation: String = "",
    @SerializedName("phone") @Expose var phone: String = "",
    @SerializedName("status") @Expose var status: String = ""
) {
    companion object {
        const val TABLE_NAME = "Location"
    }
}