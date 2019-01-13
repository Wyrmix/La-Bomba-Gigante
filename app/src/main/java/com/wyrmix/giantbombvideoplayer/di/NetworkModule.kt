package com.wyrmix.giantbombvideoplayer.di

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.wyrmix.giantbombvideoplayer.video.network.GiantbombApiClient
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

/**
 * Created by kylea
 *
 * 1/12/2019 at 4:15 AM
 */
val networkModule = module("app") {
    single {
        val httpClient = OkHttpClient.Builder()

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BASIC

        httpClient.addInterceptor(interceptor)
        httpClient.addInterceptor(StethoInterceptor())

        val cacheDir = File((androidContext()).cacheDir, "http")
        val cache = Cache(cacheDir, Companion.DISK_CACHE_SIZE.toLong())
        httpClient.cache(cache)

        httpClient.build()
    }
    single { Gson() }
    single {
        Retrofit.Builder()
                .baseUrl(Companion.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(get()))
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .client(get())
                .build()
    }
    single { get<Retrofit>().create(GiantbombApiClient::class.java) } bind GiantbombApiClient::class
}