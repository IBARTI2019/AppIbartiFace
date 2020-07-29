package com.oesvica.appibartiFace.data.model.method

import com.google.gson.annotations.SerializedName

data class Method(
    @SerializedName("cod_metodo") var codeMethod: String,
    @SerializedName("metodo") var method: String
)