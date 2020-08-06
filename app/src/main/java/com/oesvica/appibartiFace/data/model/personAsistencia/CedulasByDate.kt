package com.oesvica.appibartiFace.data.model.personAsistencia

import com.google.gson.annotations.SerializedName

data class CedulasByDate(
    @SerializedName("cedulas")
    var cedulas: List<PersonAsistencia> = emptyList(),
    @SerializedName("fecha_query")
    var dateQuery: String = ""
)