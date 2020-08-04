package com.oesvica.appibartiFace.di.module

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.oesvica.appibartiFace.data.api.AppIbartiFaceApi
import com.oesvica.appibartiFace.utils.debug
import com.oesvica.appibartiFace.utils.ibartiConverterFactory.IbartiConverterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class RetrofitModule {

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .addInterceptor {chain->
                val RETRY_LIMIT = 3
                val request: Request = chain.request()
                var response: Response? = null
                var responseOK = false
                var tryCount = 0

                while (!responseOK && tryCount < RETRY_LIMIT) {
                    try {
                        response = chain.proceed(request)
                        responseOK = response.isSuccessful
                    } catch (e: Exception) {
                        debug("Request is not successful - $tryCount ${request.url()}")
                    } finally {
                        tryCount++
                    }
                }
                // otherwise just pass the original response on
                response ?: chain.proceed(request)
            }
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides
    @Singleton
    fun gson(): Gson = GsonBuilder()
        .setLenient()
        .create()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit = Retrofit.Builder()
        .baseUrl(AppIbartiFaceApi.END_POINT)
        .client(okHttpClient)
        .addConverterFactory(IbartiConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    @Singleton
    fun provideAppIbartiFaceApi(retrofit: Retrofit): AppIbartiFaceApi =
        retrofit.create(AppIbartiFaceApi::class.java)
}