package com.oesvica.appibartiFace.data.model.auth

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LogInResponse(
    @SerializedName("logIn") @Expose var logIn: Boolean,
    @SerializedName("token") @Expose var token: String
)