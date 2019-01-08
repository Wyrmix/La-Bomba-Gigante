package com.wyrmix.giantbombvideoplayer.di

import android.app.Activity
import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bumptech.glide.util.LruCache
import com.danikula.videocache.HttpProxyCacheServer
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.wyrmix.giantbombvideoplayer.R
import com.wyrmix.giantbombvideoplayer.auth.AuthenticationViewModel
import com.wyrmix.giantbombvideoplayer.di.Companion.BASE_URL
import com.wyrmix.giantbombvideoplayer.di.Companion.DISK_CACHE_SIZE
import com.wyrmix.giantbombvideoplayer.di.Companion.parseRawJson
import com.wyrmix.giantbombvideoplayer.video.database.AppDatabase
import com.wyrmix.giantbombvideoplayer.video.database.Video
import com.wyrmix.giantbombvideoplayer.video.details.VideoDetailsViewModel
import com.wyrmix.giantbombvideoplayer.video.list.VideoBrowseViewModel
import com.wyrmix.giantbombvideoplayer.video.models.VideoCategoryResult
import com.wyrmix.giantbombvideoplayer.video.models.VideoShowResult
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.concurrent.Executors


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
            single { Gson() }
            single {
                Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create(get()))
                        .addCallAdapterFactory(CoroutineCallAdapterFactory())
                        .client(get())
                        .build()
            }
            single { get<Retrofit>().create(GiantbombApiClient::class.java) } bind GiantbombApiClient::class

            single { NetworkManager(get()) }

            single<RoomDatabase.Callback> {
                object: RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        Timber.i("db created")
                        super.onCreate(db)
                        Executors.newSingleThreadScheduledExecutor().execute {
                            Timber.i("seeding database")
                            try {
                                val appDb = get<AppDatabase>()
                                val shows = get<Gson>().fromJson<VideoShowResult>(parseRawJson(get(), R.raw.video_shows), VideoShowResult::class.java)
                                Timber.d("shows [$shows]")
                                appDb.videoShowDao().insertVideoShow(*shows.results.toTypedArray())
                                val categories = get<Gson>().fromJson<VideoCategoryResult>(parseRawJson(get(), R.raw.video_categories), VideoCategoryResult::class.java)
                                Timber.d("categories [$categories]")
                                appDb.videoCategoryDao().insertVideoCategory(*categories.results.toTypedArray())
                            } catch (t: Throwable) {
                                Timber.e(t)
                            }
                        }
                    }
                }
            }
            single {
                Room.databaseBuilder(get(), AppDatabase::class.java, "LaBombaGigante")
                    .fallbackToDestructiveMigration()
                    .addCallback(get())
                    .build()
            }
            single { get<AppDatabase>().videoDao() }
            single { get<AppDatabase>().videoCategoryDao() }
            single { get<AppDatabase>().videoShowDao() }

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
object Companion {
    const val BASE_URL = "https://www.giantbomb.com/api/"
    const val DISK_CACHE_SIZE = 50 * 1024 * 1024 // 50MB

    fun parseRawJson(context: android.content.Context, resId: Int): String {

        val inputStream = context.resources.openRawResource(resId)
        val byteArrayOutputStream = ByteArrayOutputStream()

        var ctr: Int
        try {
            ctr = inputStream.read()
            while (ctr != -1) {
                byteArrayOutputStream.write(ctr)
                ctr = inputStream.read()
            }
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return byteArrayOutputStream.toString()
    }
}