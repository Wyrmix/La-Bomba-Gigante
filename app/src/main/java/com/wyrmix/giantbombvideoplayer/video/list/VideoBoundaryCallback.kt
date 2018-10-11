package com.wyrmix.giantbombvideoplayer.video.list

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.wyrmix.giantbombvideoplayer.video.database.Video
import com.wyrmix.giantbombvideoplayer.video.database.VideoDao
import com.wyrmix.giantbombvideoplayer.video.models.NetworkState
import com.wyrmix.giantbombvideoplayer.video.network.ApiRepository
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.IO
import kotlinx.coroutines.experimental.async

class VideoBoundaryCallback(
        private val api: ApiRepository,
        private val scope: CoroutineScope,
        private val videoDao: VideoDao)
    : PagedList.BoundaryCallback<Video>() {
    val networkState = MutableLiveData<NetworkState>()

    fun retryFailed() {

    }

    override fun onZeroItemsLoaded() {
        scope.async(Dispatchers.IO) {
            api.getVideos()
        }
    }

    override fun onItemAtEndLoaded(itemAtEnd: Video) {
        scope.async(Dispatchers.IO) {
            api.getVideosPaged(videoDao.getNumberOfRows())
        }
    }

    override fun onItemAtFrontLoaded(itemAtFront: Video) {
        scope.async(Dispatchers.IO) {
            api.getVideosPaged(0)
        }
    }
}