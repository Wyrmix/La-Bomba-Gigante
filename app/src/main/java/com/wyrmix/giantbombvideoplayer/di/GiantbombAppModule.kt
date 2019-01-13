package com.wyrmix.giantbombvideoplayer.di

import android.app.Activity
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Environment
import com.bumptech.glide.util.LruCache
import com.danikula.videocache.HttpProxyCacheServer
import com.tonyodev.fetch2.Fetch
import com.tonyodev.fetch2.FetchConfiguration
import com.tonyodev.fetch2okhttp.OkHttpDownloader
import com.tonyodev.fetch2rx.RxFetch
import com.wyrmix.giantbombvideoplayer.R
import com.wyrmix.giantbombvideoplayer.auth.AuthenticationViewModel
import com.wyrmix.giantbombvideoplayer.video.database.AppDatabase
import com.wyrmix.giantbombvideoplayer.video.database.Video
import com.wyrmix.giantbombvideoplayer.video.details.VideoDetailsViewModel
import com.wyrmix.giantbombvideoplayer.video.downloads.DownloadNotificationManager
import com.wyrmix.giantbombvideoplayer.video.downloads.DownloadsViewModel
import com.wyrmix.giantbombvideoplayer.video.downloads.VideoDownload
import com.wyrmix.giantbombvideoplayer.video.list.VideoBrowseViewModel
import com.wyrmix.giantbombvideoplayer.video.network.ApiRepository
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import timber.log.Timber


/**
 * Koin main module
 */
val appModule = module("app") {
    single { androidContext().getSharedPreferences("GiantbombApp", android.content.Context.MODE_PRIVATE) }

    factory {
        FetchConfiguration.Builder(get())
        .setDownloadConcurrentLimit(10)
        .setNotificationManager(DownloadNotificationManager(get()))
        .setHttpDownloader(OkHttpDownloader(get<OkHttpClient>()))
        .build()
    }

    single { RxFetch.getRxInstance(get()) }

    single { Fetch.getInstance(get()) }

    single { NetworkManager(get()) }

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
        single { ApiRepository(get(), get(), get(), get(), get()) }
        viewModel { VideoBrowseViewModel(get(), get(), get(), get()) }
    }

    module("details") {
        viewModel { (videoDownload: VideoDownload) ->
            val downloadLocationKey = get<android.content.Context>().getString(R.string.pref_key_settings_download_private)
            VideoDetailsViewModel(videoDownload.video, videoDownload.download, get(), get(), get(), downloadLocationKey, androidContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES)!!.path)
        }
    }

    module("downloads") {
        viewModel { DownloadsViewModel(get(), get(), get(), get()) }
    }

    module("playback") {
        factory { HttpProxyCacheServer.Builder(androidContext())
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

/**
 * Module constants
 */
object Companion {
    const val BASE_URL = "https://www.giantbomb.com/api/"
    const val DISK_CACHE_SIZE = 50 * 1024 * 1024 // 50MB
}