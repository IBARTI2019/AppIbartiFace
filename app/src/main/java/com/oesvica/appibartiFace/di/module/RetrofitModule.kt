package com.oesvica.appibartiFace.di.module

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.oesvica.appibartiFace.data.api.AppIbartiFaceApi
import com.oesvica.appibartiFace.utils.ibartiConverterFactory.IbartiConverterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
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
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)// TODO add interceptor po provide for more time in case of a long request like aptos/noAptos request
            .addNetworkInterceptor(StethoInterceptor())
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