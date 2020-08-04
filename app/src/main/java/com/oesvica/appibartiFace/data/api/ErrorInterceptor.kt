package com.oesvica.appibartiFace.data.api

import com.oesvica.appibartiFace.utils.debug
import com.oesvica.appibartiFace.utils.ibartiConverterFactory.LogInException
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody




class ErrorInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        debug("hello2")
        val request = chain.request()
        val response = chain.proceed(request)
        val responseBody = response.body()
        val responseBodyString = response.body()!!.string()
        debug("hello3 $responseBodyString")
        if(responseBodyString.matches("^.+\$".toRegex())){
            debug("hey this is a plain text response")
            throw LogInException(responseBodyString)
        }
        return response.newBuilder()
            .body(ResponseBody.create(responseBody?.contentType(), responseBodyString.toByteArray()))
            .build()
        //return response
    }
}