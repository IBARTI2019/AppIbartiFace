package com.oesvica.appibartiFace.data.model.personAsistencia

import com.google.gson.annotations.SerializedName
import com.oesvica.appibartiFace.data.api.AppIbartiFaceApi

data class Doc(
    @SerializedName("_id")
    var id: String = "",
    @SerializedName("cedula")
    var cedula: String = "",
    @SerializedName("fecha")
    var date: String = "",
    @SerializedName("fecha_captura")
    var dateCapture: String = "",
    @SerializedName("foto")
    var photo: String = "",
    @SerializedName("hora")
    var time: String = ""
)
fun Doc.fullPhotoUrl(): String = "${AppIbartiFaceApi.END_POINT}view$photo"