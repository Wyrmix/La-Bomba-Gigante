package com.wyrmix.giantbombvideoplayer.video.downloads

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Fetch
import com.tonyodev.fetch2core.Func
import com.tonyodev.fetch2rx.RxFetch
import com.wyrmix.giantbombvideoplayer.video.database.Video
import com.wyrmix.giantbombvideoplayer.video.database.VideoDao
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

/**
 * Created by kylea
 *
 * 1/11/2019 at 4:13 PM
 */
class DownloadsViewModel(app: Application, private val rxFetch: RxFetch, private val fetch: Fetch, private val videoDao: VideoDao): AndroidViewModel(app), CoroutineScope {
    private var sub: Disposable? = null
    private val liveData: MutableLiveData<List<VideoDownload>> = MutableLiveData()

    fun logDownloadProgress() {
        sub = rxFetch.getDownloads().flowable.subscribeBy(
                onNext = { Timber.d("onNext($it)") },
                onError = { Timber.e("onError($it)") },
                onComplete = { Timber.d("onComplete()") }
        )
    }

    fun refresh(): Observable<List<Download>> {
        return rxFetch.getDownloads().observable
    }

    fun getDownloads() {
        liveData.value = listOf()
        val job = async(Dispatchers.IO) {
            Timber.v("getting downloads")
            val list = mutableListOf<Download>()
            fetch.getDownloads(Func { l ->
//                liveData.value = list.map {
//                    val video = getVideoById(it.tag?.toLong() ?: -1)
//                    VideoDownloads(video, it)
//                }
                Timber.d("fetch returned [$l]")
                list.addAll(l)
            })
            delay(100)
            val ret = list.map { VideoDownload(getVideoById(it.tag?.toLong() ?: -1), it) }
            Timber.d("shoving downloads into live data [$ret]")
            liveData.postValue(ret)
        }
        this + job
    }

    private suspend fun getVideoById(id: Long): Video {
        Timber.v("launching db coroutine")
        return withContext(Dispatchers.IO) {
            Timber.d("getting video for id $id")
            videoDao.getVideoById(id)
        }
    }

    fun downloads(): LiveData<List<VideoDownload>> {
        Timber.v("returning livedata for downloads")
        return liveData
    }

    fun dispose() {
        sub?.dispose()
    }

    override val coroutineContext: CoroutineContext
        get() = GlobalScope.coroutineContext
}