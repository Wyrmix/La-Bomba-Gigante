package com.wyrmix.giantbombvideoplayer.video.network

import android.content.SharedPreferences
import com.wyrmix.giantbombvideoplayer.video.database.*
import com.wyrmix.giantbombvideoplayer.video.models.VideoCategoryResult
import com.wyrmix.giantbombvideoplayer.video.models.VideoResult
import com.wyrmix.giantbombvideoplayer.video.models.VideoShowResult
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.selects.select
import timber.log.Timber
import kotlin.coroutines.experimental.CoroutineContext

const val defaultKey = "No Saved API Key"
const val json = "json"

/**
 * Class to handle using [GiantbombApiClient] properly that makes an HTTP request each time data
 * is requested, and handles inserting it into the database on a background thread.
 *
 * Use with something like the paging library to handle subscribing to the database and calling
 * the endpoint to refresh data.
 */
class ApiRepository(
        private val giantbombApiClient: GiantbombApiClient,
        private val sharedPreferences: SharedPreferences,
        private val videoDao: VideoDao,
        private val videoShowDao: VideoShowDao,
        private val videoCategoryDao: VideoCategoryDao,
        private val videoJoinDao: VideoJoinDao
): CoroutineScope {

    val key: String = sharedPreferences.getString("API_KEY", defaultKey) ?: defaultKey

//    init { if (key == defaultKey) throw IllegalStateException("created repository instance without saving key to shared prefs") }

    suspend fun getVideoShows(): VideoShowResult {
        val db = produce {
            send(videoShowDao.selectAll())
        }
        val network = produce {
            send(giantbombApiClient.getVideoShows(key, json).await())
        }

        return videoShowsFromDbOrNetwork(db, network) ?: VideoShowResult()
    }

    private suspend inline fun videoShowsFromDbOrNetwork(db: ReceiveChannel<List<VideoShow>>, network: ReceiveChannel<VideoShowResult>): VideoShowResult? {
        return select {
            network.onReceiveOrNull {
                if (it != null) {
                    withContext(Dispatchers.IO) {
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

    suspend fun getVideoCategories(): VideoCategoryResult {
        val db = produce {
            send(videoCategoryDao.selectAll())
        }
        val network = produce {
            send(giantbombApiClient.getVideoCategories(key, json).await())
        }

        return videoCategoriesFromDbOrNetwork(db, network) ?: VideoCategoryResult()
    }

    private suspend inline fun videoCategoriesFromDbOrNetwork(db: ReceiveChannel<List<VideoCategory>>, network: ReceiveChannel<VideoCategoryResult>): VideoCategoryResult? {
        return select {
            network.onReceiveOrNull {
                if (it != null) {
                    withContext(Dispatchers.IO) {
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

    suspend fun getVideos(): VideoResult {
        return giantbombApiClient.getVideos(key, json).await().apply {
            Timber.i("Inserting [${results.size}] records into database")
            Timber.v("Videos [${results.toTypedArray()}]")
            videoDao.insertVideo(*results.toTypedArray())

            this.results.forEach { video ->
                videoDao.insertVideo(video)
                val category = if (video.videoCategories.isNotEmpty()) {
                    video.videoCategories.firstOrNull()
                } else {
                    null
                }

                category?.apply { videoCategoryDao.insertVideoCategory(this) }
                video.videoShow?.apply { videoShowDao.insertVideoShow(this) }
                videoJoinDao.insert(VideoJoin(videoId = video.id, videoCategoryId = category?.id, videoShowId = video.videoShow?.id))
            }
        }
    }

    suspend fun getVideosPaged(pageSize: Int): VideoResult {
        val filter = sharedPreferences.getStringSet("filter", emptySet())?.joinToString() ?: ""
        return if (filter.isNotEmpty()) {
            giantbombApiClient.getVideosPaged(key, json, pageSize, filter).await().apply {
                videoDao.insertVideo(*results.toTypedArray())
            }
        } else {
            giantbombApiClient.getVideosPaged(key, json, pageSize).await().apply {
                videoDao.insertVideo(*results.toTypedArray())
            }
        }
    }

    override val coroutineContext: CoroutineContext
        get() = GlobalScope.coroutineContext
}
