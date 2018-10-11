package com.wyrmix.giantbombvideoplayer.video.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.wyrmix.giantbombvideoplayer.video.database.VideoDao
import com.wyrmix.giantbombvideoplayer.video.models.VideoCategoryResult
import com.wyrmix.giantbombvideoplayer.video.models.VideoResult
import com.wyrmix.giantbombvideoplayer.video.models.VideoShowResult
import com.wyrmix.giantbombvideoplayer.video.network.ApiRepository
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.GlobalScope
import kotlin.coroutines.experimental.CoroutineContext

class VideoBrowserViewModel(
        private val videoDao: VideoDao,
        private val apiRepository: ApiRepository,
        val app: Application
): AndroidViewModel(app), CoroutineScope {
    private val videoItemRepository = VideoRepository(videoDao, apiRepository, this)

    suspend fun getVideos(): VideoResult {
        return apiRepository.getVideos()
    }

    suspend fun getVideoShows(): VideoShowResult {
        return apiRepository.getVideoShows()
    }

    suspend fun getVideoCategories(): VideoCategoryResult {
        return apiRepository.getVideoCategories()
    }

    private val startTrigger = MutableLiveData<Unit>()
    private val repoResult = Transformations.map(startTrigger) {
        videoItemRepository.videos()
    }
    val posts = Transformations.switchMap(repoResult) { it.pagedList }!!
    val networkState = Transformations.switchMap(repoResult) { it.networkState }!!
    val refreshState = Transformations.switchMap(repoResult) { it.refreshState }!!

    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }

    fun showData() {
        startTrigger.value = Unit
    }

    fun retry() {
        val listing = repoResult?.value
        listing?.retry?.invoke()
    }

    override val coroutineContext: CoroutineContext
        get() = GlobalScope.coroutineContext
}