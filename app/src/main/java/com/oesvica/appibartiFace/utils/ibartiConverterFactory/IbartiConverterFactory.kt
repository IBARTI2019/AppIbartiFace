package com.oesvica.appibartiFace.utils.ibartiConverterFactory

import com.google.gson.Gson
import com.oesvica.appibartiFace.data.model.auth.LogInResponse
import com.oesvica.appibartiFace.utils.debug
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class IbartiConverterFactory
private constructor() : Converter.Factory() {

    companion object {
        @JvmStatic
        fun create() = IbartiConverterFactory()
    }

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        debug(
            "responseBodyConverter(\n" +
                    "        $type: Type,\n" +
                    "        annotations: Array<Annotation>,\n" +
                    "        retrofit: Retrofit"
        )
        if (type.toString() == "class ${LogInResponse::class.qualifiedName}"){ // look for a better way to compare this
            return BodyConverter()
        }
        return super.responseBodyConverter(type, annotations, retrofit)
    }

}

class BodyConverter : Converter<ResponseBody, LogInResponse> {
    override fun convert(value: ResponseBody): LogInResponse? {
        var logInResponse: LogInResponse? = null
        val response = value.string()
        value.use {
            debug("converting $response")
            if (response == "Ya se Encuentra Logeado" || response == "Usuario Invalido") {
                it.close()
                throw LogInException(response)
            }
            logInResponse = Gson().fromJson<LogInResponse>(response, LogInResponse::class.java)
        }
        return logInResponse
    }

}

class LogInException(message: String): Exception(message)