package com.oesvica.appibartiFace.data.model.standby

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Prediction(
    @SerializedName("cedula") @Expose var cedula: String,
    @SerializedName("distancia") @Expose var distancia: String,
    @SerializedName("semajanza") @Expose var semejanza: String,
    @SerializedName("url") @Expose var url: String
) {
    val completeUrl: String
        get() = "http://oesvica.ddns.net:5003${url}"

    override fun toString(): String {
        return "Prediction(cedula='$cedula', distancia='$distancia', semejanza='$semejanza', completeUrl='$completeUrl')"
    }
}