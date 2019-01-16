package com.wyrmix.giantbombvideoplayer.di

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.wyrmix.giantbombvideoplayer.R
import com.wyrmix.giantbombvideoplayer.video.database.AppDatabase
import com.wyrmix.giantbombvideoplayer.video.models.VideoCategoryResult
import com.wyrmix.giantbombvideoplayer.video.models.VideoShowResult
import org.koin.dsl.module.module
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.concurrent.Executors

/**
 * Created by kylea
 *
 * 1/12/2019 at 4:15 AM
 */
val databaseModule = module("app") {
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
}

private fun parseRawJson(context: android.content.Context, resId: Int): String {

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
