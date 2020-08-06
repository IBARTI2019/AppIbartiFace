package com.oesvica.appibartiFace.data.api

import com.oesvica.appibartiFace.utils.debug
import com.oesvica.appibartiFace.utils.ibartiConverterFactory.LogInException
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import java.util.concurrent.TimeoutException


class RetryInterceptor : Interceptor {

    companion object {
        const val RETRY_LIMIT = 3
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        var response: Response? = null
        var responseOK = false
        var tryCount = 0

        while (!responseOK && tryCount < RETRY_LIMIT - 1) {
            debug("trying request ${request.url()} $tryCount time")
            try {
                response = chain.proceed(request)
                responseOK = response.isSuccessful
            } catch (e: TimeoutException) {
                debug("Request error is timeout - $tryCount ${request.url()}")
                e.printStackTrace()
            } catch (e: Exception) {
                debug("Request error is Exception ${response?.isSuccessful}- $tryCount ${request.url()}")
                e.printStackTrace()
                break
            } finally {
                tryCount++
            }
        }
        // otherwise just pass the original response on
        return response ?: chain.proceed(request)
    }
}