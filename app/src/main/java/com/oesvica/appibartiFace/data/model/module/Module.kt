package com.oesvica.appibartiFace.data.model.module

import com.google.gson.annotations.SerializedName

data class Module(
    @SerializedName("cod_modulo") var codeModule: String,
    @SerializedName("modulo") var module: String
)