package com.wyrmix.giantbombvideoplayer.video.network

import android.content.SharedPreferences
import com.wyrmix.giantbombvideoplayer.video.database.VideoCategoryDao
import com.wyrmix.giantbombvideoplayer.video.database.VideoDao
import com.wyrmix.giantbombvideoplayer.video.database.VideoShowDao
import com.wyrmix.giantbombvideoplayer.video.models.*
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.GlobalScope
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
        sharedPreferences: SharedPreferences,
        private val videoDao: VideoDao,
        private val videoShowDao: VideoShowDao,
        private val videoCategoryDao: VideoCategoryDao
): CoroutineScope {

    val key: String = sharedPreferences.getString("API_KEY", defaultKey) ?: defaultKey

    init { if (key == defaultKey) throw IllegalStateException("created repository instance without saving key to shared prefs") }

    suspend fun getVideoShows(): VideoShowResult {
        return giantbombApiClient.getVideoShows(key, json).await().apply {
            videoShowDao.insertVideoShow(*results.toTypedArray())
        }
    }

    suspend fun getVideoCategories(): VideoCategoryResult {
        return giantbombApiClient.getVideoCategories(key, json).await().apply {
            videoCategoryDao.insertVideoCategory(*results.toTypedArray())
        }
    }

    suspend fun getVideos(): VideoResult {
        return giantbombApiClient.getVideos(key, json).await().apply {
            videoDao.insertVideo(*results.toTypedArray())
        }
    }

    suspend fun getVideosPaged(pageSize: Int): VideoResult {
        return giantbombApiClient.getVideosPaged(key, json, pageSize).await().apply {
            videoDao.insertVideo(*results.toTypedArray())
        }
    }

    override val coroutineContext: CoroutineContext
        get() = GlobalScope.coroutineContext
}
