package com.oesvica.appibartiFace.data.model.role

import com.google.gson.annotations.SerializedName

data class Roll(
    @SerializedName("cod_roll") var codeRoll: String,
    @SerializedName("descripcion") var description: String,
    @SerializedName("SU") var su: Boolean
)