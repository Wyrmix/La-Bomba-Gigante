package com.wyrmix.giantbombvideoplayer.video.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.wyrmix.giantbombvideoplayer.video.database.VideoDao
import com.wyrmix.giantbombvideoplayer.video.models.VideoCategoryResult
import com.wyrmix.giantbombvideoplayer.video.models.VideoResult
import com.wyrmix.giantbombvideoplayer.video.models.VideoShowResult
import com.wyrmix.giantbombvideoplayer.video.models.VideoType
import com.wyrmix.giantbombvideoplayer.video.network.ApiRepository
import kotlinx.coroutines.experimental.*
import okhttp3.OkHttpClient
import kotlin.coroutines.experimental.CoroutineContext

class VideoBrowseViewModel(
        private val videoDao: VideoDao,
        private val apiRepository: ApiRepository,
        private val okHttp: OkHttpClient,
        app: Application
): AndroidViewModel(app), CoroutineScope {

    private val videoItemRepository = VideoRepository(videoDao, apiRepository, this)

    val videoTypes = MutableLiveData<List<VideoType>>()
    val shows = MutableLiveData<VideoShowResult>()
    val categories = MutableLiveData<VideoCategoryResult>()
    val videos = MutableLiveData<VideoResult>()

    suspend fun getVideos(): VideoResult {
        return apiRepository.getVideos()
    }

    fun fetchCategoriesAndShows() {
        val job = async(Dispatchers.IO) {
            val shows = apiRepository.getVideoShows().results
            val categories = apiRepository.getVideoCategories().results

            val types = mutableListOf<VideoType>()
            types.addAll(shows)
            types.addAll(categories)

            videoTypes.postValue(types)
        }
        this + job
    }

    fun getVideoShows() {
        val job = async(Dispatchers.IO) {
            shows.postValue(apiRepository.getVideoShows())
        }
        this + job
    }

    fun getVideoCategories() {
        val job = async(Dispatchers.IO) {
            categories.postValue(apiRepository.getVideoCategories())
        }
        this + job
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