package com.wyrmix.giantbombvideoplayer.video.network

import android.content.SharedPreferences
import com.wyrmix.giantbombvideoplayer.video.models.*
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.selects.select
import kotlinx.coroutines.experimental.withContext
import timber.log.Timber
import kotlin.coroutines.experimental.CoroutineContext

const val defaultKey = "No Saved API Key"
const val json = "json"

class ApiRepository(
        private val giantbombApiClient: GiantbombApiClient,
        private val sharedPreferences: SharedPreferences,
        private val videoDao: VideoDao,
        private val videoShowDao: VideoShowDao,
        private val videoCategoryDao: VideoCategoryDao
): CoroutineScope {

    val key: String = sharedPreferences.getString("API_KEY", defaultKey) ?: defaultKey

    init {
        if (key == defaultKey) throw IllegalStateException("created repository instance without saving key to shared prefs")
    }

    suspend fun getVideoShows(): VideoShowResult {
        val db = produce {
            send(videoShowDao.selectAll())
        }
        val network = produce {
            send(giantbombApiClient.getVideoShows(key, json).await())
        }

        return videoShowsFromDbOrNetwork(db, network) ?: VideoShowResult()
    }

    suspend fun getVideoCategories(): VideoCategoryResult {
        val db = produce {
            send(videoCategoryDao.selectAll())
        }
        val network = produce {
            send(giantbombApiClient.getVideoCategories(key, json).await())
        }

        return videoCategoriesFromDbOrNetwork(db, network) ?: VideoCategoryResult()
    }

    suspend fun getVideos(): VideoResult {
        val db = produce {
            send(videoDao.selectAll())
        }
        val network = produce {
            send(giantbombApiClient.getVideos(key, json).await())
        }

        val videos = videosFromDbOrNetwork(db, network) ?: VideoResult()
        Timber.i("video results [${videos.results.size}]")

        return videos
    }

    private suspend inline fun videoCategoriesFromDbOrNetwork(db: ReceiveChannel<List<VideoCategory>>,network: ReceiveChannel<VideoCategoryResult>): VideoCategoryResult? {
        return select {
            network.onReceiveOrNull {
                if (it != null) {
                    withContext(Dispatchers.Default) {
                        Timber.d("inserting network values into db")
                        videoCategoryDao.insertVideoCategory(*it.results.toTypedArray())
                    }
                }
                it
            }
            db.onReceiveOrNull {
                if (it == null) null
                else VideoCategoryResult(results = it)
            }
        }
    }

    private suspend inline fun videoShowsFromDbOrNetwork(db: ReceiveChannel<List<VideoShow>>, network: ReceiveChannel<VideoShowResult>): VideoShowResult? {
        return select {
            network.onReceiveOrNull {
                if (it != null) {
                    withContext(Dispatchers.Default) {
                        Timber.d("inserting network values into db")
                        videoShowDao.insertVideoShow(*it.results.toTypedArray())
                    }
                }
                it
            }
            db.onReceiveOrNull {
                if (it == null) null
                else VideoShowResult(results = it)
            }
        }
    }

    private suspend inline fun videosFromDbOrNetwork(db: ReceiveChannel<List<Video>>, network: ReceiveChannel<VideoResult>): VideoResult? {
        return select {
            network.onReceiveOrNull {
                Timber.i("onReceive from network [${it?.results?.size}]")
                if (it != null) {
                    withContext(Dispatchers.Default) {
                        Timber.d("inserting network values into db")
                        videoDao.insertVideo(*it.results.toTypedArray())
                    }
                }

                it
            }
            db.onReceiveOrNull {
                Timber.i("onReceive from db [${it?.size}]")
                if (it == null) null
                else VideoResult(results = it)
            }
        }
    }

    override val coroutineContext: CoroutineContext
        get() = GlobalScope.coroutineContext
}
