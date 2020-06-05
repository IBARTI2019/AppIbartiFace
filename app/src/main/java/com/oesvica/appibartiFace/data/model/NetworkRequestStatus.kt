package com.oesvica.appibartiFace.data.model

data class NetworkRequestStatus(var isOngoing: Boolean, var error: Throwable? = null)