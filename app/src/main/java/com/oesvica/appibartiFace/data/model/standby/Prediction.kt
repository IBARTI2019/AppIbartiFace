package com.oesvica.appibartiFace.data.model.standby

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.oesvica.appibartiFace.data.api.AppIbartiFaceApi
import com.oesvica.appibartiFace.data.api.AppIbartiFaceApi.Companion.END_POINT

data class Prediction(
    @SerializedName("cedula") @Expose var cedula: String,
    @SerializedName("distancia") @Expose var distancia: String,
    @SerializedName("semejanza") @Expose var semejanza: String,
    @SerializedName("url") @Expose var url: String
) {
    val completeUrl: String
        get() = "${END_POINT.dropLast(1)}/view${url}"

    override fun toString(): String {
        return "Prediction(cedula='$cedula', distancia='$distancia', semejanza='$semejanza', completeUrl='$completeUrl')"
    }
}