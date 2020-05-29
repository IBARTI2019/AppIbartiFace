package com.oesvica.appibartiFace.data.model

data class Result<out T>(val success: T? = null, val error: Throwable? = null)