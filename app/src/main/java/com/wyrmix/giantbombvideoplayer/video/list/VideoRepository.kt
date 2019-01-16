package com.wyrmix.giantbombvideoplayer.video.list

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.wyrmix.giantbombvideoplayer.video.database.Video
import com.wyrmix.giantbombvideoplayer.video.database.VideoDao
import com.wyrmix.giantbombvideoplayer.video.models.Listing
import com.wyrmix.giantbombvideoplayer.video.models.NetworkState
import com.wyrmix.giantbombvideoplayer.video.network.ApiRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.plus

class VideoRepository(
        val db: VideoDao,
        private val api: ApiRepository,
        private val scope: CoroutineScope,
        private val networkPageSize: Int = DEFAULT_NETWORK_PAGE_SIZE) {
    companion object {
        private const val DEFAULT_NETWORK_PAGE_SIZE = 100
    }

    /**
     * When refresh is called, we simply run a fresh network request and when it arrives, clear
     * the database table and insert all new items in a transaction.
     * <p>
     * Since the PagedList already uses a database bound data source, it will automatically be
     * updated after the database transaction is finished.
     */
    @MainThread
    private fun refresh(): LiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING

        scope + scope.async(Dispatchers.IO) {
            api.getVideos()
        }

        networkState.value = NetworkState.LOADED
        return networkState
    }

    /**
     * Returns a listing of videos
     */
    @MainThread
    fun videos(): Listing<Video> {
        // create a boundary callback which will observe when the user reaches to the edges of
        // the list and update the database with extra data.
        val boundaryCallback = VideoBoundaryCallback(api, scope, db)
        // create a data source factory from Room
        val dataSourceFactory = db.selectPaged()
        val config = PagedList.Config.Builder()
                .setPageSize(10)
                .setInitialLoadSizeHint(100)
                .setEnablePlaceholders(true)
                .build()
        val builder = LivePagedListBuilder(dataSourceFactory, config).setBoundaryCallback(boundaryCallback)

        // we are using a mutable live data to trigger refresh requests which eventually calls
        // refresh method and gets a new live data. Each refresh request by the user becomes a newly
        // dispatched data in refreshTrigger
        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger) {
            refresh()
        }

        return Listing(
                pagedList = builder.build(),
                networkState = boundaryCallback.networkState,
                retry = {
                    boundaryCallback.retryFailed()
                },
                refresh = {
                    refreshTrigger.value = Unit
                },
                refreshState = refreshState
        )
    }
}