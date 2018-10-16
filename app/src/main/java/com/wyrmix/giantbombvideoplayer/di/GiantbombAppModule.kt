package com.wyrmix.giantbombvideoplayer.di

import android.app.Activity
import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.room.Room
import com.bumptech.glide.util.LruCache
import com.danikula.videocache.HttpProxyCacheServer
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import com.wyrmix.giantbombvideoplayer.auth.AuthenticationViewModel
import com.wyrmix.giantbombvideoplayer.di.Context.BASE_URL
import com.wyrmix.giantbombvideoplayer.di.Context.DISK_CACHE_SIZE
import com.wyrmix.giantbombvideoplayer.video.database.AppDatabase
import com.wyrmix.giantbombvideoplayer.video.database.Video
import com.wyrmix.giantbombvideoplayer.video.details.VideoDetailsViewModel
import com.wyrmix.giantbombvideoplayer.video.list.VideoBrowseViewModel
import com.wyrmix.giantbombvideoplayer.video.network.ApiRepository
import com.wyrmix.giantbombvideoplayer.video.network.GiantbombApiClient
import com.wyrmix.giantbombvideoplayer.video.network.NetworkManager
import com.wyrmix.giantbombvideoplayer.video.player.VideoPlayerViewModel
import io.palaima.debugdrawer.DebugDrawer
import io.palaima.debugdrawer.actions.ActionsModule
import io.palaima.debugdrawer.actions.ButtonAction
import io.palaima.debugdrawer.commons.BuildModule
import io.palaima.debugdrawer.commons.DeviceModule
import io.palaima.debugdrawer.commons.NetworkModule
import io.palaima.debugdrawer.commons.SettingsModule
import io.palaima.debugdrawer.logs.LogsModule
import io.palaima.debugdrawer.network.quality.NetworkQualityModule
import io.palaima.debugdrawer.okhttp3.OkHttp3Module
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.File


/**
 * Koin main module
 */
val appModule = module {
        module("app") {
            single { get<android.content.Context>().getSharedPreferences("GiantbombApp", android.content.Context.MODE_PRIVATE) }

            single {
                val httpClient = OkHttpClient.Builder()

                val interceptor = HttpLoggingInterceptor()
                interceptor.level = HttpLoggingInterceptor.Level.BASIC

                httpClient.addInterceptor(interceptor)
                httpClient.addInterceptor(StethoInterceptor())

                val cacheDir = File((get() as android.content.Context).cacheDir, "http")
                val cache = Cache(cacheDir, DISK_CACHE_SIZE.toLong())
                httpClient.cache(cache)

                httpClient.build()
            }
            single {
                Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create(Gson()))
                        .addCallAdapterFactory(CoroutineCallAdapterFactory())
                        .client(get())
                        .build()
            }
            single { get<Retrofit>().create(GiantbombApiClient::class.java) } bind GiantbombApiClient::class

            single { NetworkManager(get()) }

            single { Room.databaseBuilder(get(), AppDatabase::class.java, "LaBombaGigante").fallbackToDestructiveMigration().build() }
            single { get<AppDatabase>().videoDao() }
            single { get<AppDatabase>().videoCategoryDao() }
            single { get<AppDatabase>().videoShowDao() }
            single { get<AppDatabase>().videoJoinDao() }

            single<LruCache<String, Bitmap>> {
                object : LruCache<String, Bitmap>(1024) {
                    override fun getSize(image: Bitmap?): Int {
                        return image?.byteCount ?: 0
                    }

                    override fun get(key: String): Bitmap? {
                        Timber.d("found hit in LRU cache for [$key]")
                        return super.get(key)
                    }
                }
            }

            module("auth") {
                viewModel { AuthenticationViewModel(get(), get(), get()) }
            }

            module("browse") {
                single { ApiRepository(get(), get(), get(), get(), get(), get()) }
                viewModel { VideoBrowseViewModel(get(), get(), get(), get()) }
            }

            module("details") {
                viewModel { (video: Video) -> VideoDetailsViewModel(video, get()) }
            }

            module("playback") {
                factory { HttpProxyCacheServer.Builder((get() as android.content.Context))
                        .maxCacheSize((1024 * 1024 * 1024).toLong()) // 1 Gb for cache
                        .build()
                }

                viewModel { (video: Video) -> VideoPlayerViewModel(video, get()) }
            }

            module("debug") {
                single { (activity: Activity) ->
                    val dbListener: () -> Unit = {
                        GlobalScope.launch(Dispatchers.Default) {
                            get<AppDatabase>().clearAllTables()
                        }
                    }

                    val prefsListener: () -> Unit = {
                        get<SharedPreferences>().edit().clear().apply()
                    }

                    val filterListener: () -> Unit = {
                        get<SharedPreferences>().edit().putStringSet("filter", null).apply()
                    }

                    DebugDrawer.Builder(activity)
                            .modules(
                                    ActionsModule(ButtonAction("Clear database", dbListener)),
                                    ActionsModule(ButtonAction("Clear prefs", prefsListener)),
                                    ActionsModule(ButtonAction("Clear filters", filterListener)),
                                    DeviceModule(),
                                    BuildModule(),
                                    NetworkModule(),
                                    SettingsModule(),
                                    OkHttp3Module(get()),
                                    NetworkQualityModule(androidContext()),
                                    LogsModule()
                            ).build()
                }
            }
        }
}

/**
 * Module constants
 */
object Context {
    const val BASE_URL = "https://www.giantbomb.com/api/"
    const val DISK_CACHE_SIZE = 50 * 1024 * 1024 // 50MB
}