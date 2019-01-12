package com.wyrmix.giantbombvideoplayer.video.details

import android.content.SharedPreferences
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.ViewModel
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.DownloadBlock
import com.tonyodev.fetch2core.Func2
import com.tonyodev.fetch2rx.RxFetch
import com.wyrmix.giantbombvideoplayer.auth.API_KEY
import com.wyrmix.giantbombvideoplayer.video.database.Video
import timber.log.Timber
import java.io.File

class VideoDetailsViewModel(
        val video: Video,
        val download: Download?,
        private val prefs: SharedPreferences,
        private val rxFetch: RxFetch,
        private val fetch: Fetch,
        private val downloadLocationKey: String,
        private val privateStorage: String
): ViewModel() {
    private var currentUrl: String? = ""
    private var currentQuality: VideoQuality = VideoQuality.High

    init {
        Timber.d("Video: $video, Download: $download")
    }

    val listener = object : FetchListener {
        override fun onAdded(download: Download) {
            Timber.d("added $download")
        }

        override fun onCancelled(download: Download) {
            Timber.d("cancelled $download")
        }

        override fun onCompleted(download: Download) {
            Timber.d("completed $download")
        }

        override fun onDeleted(download: Download) {
            Timber.d("deleted $download")
        }

        override fun onDownloadBlockUpdated(download: Download, downloadBlock: DownloadBlock, totalBlocks: Int) {
            Timber.d("download block updated $download, $downloadBlock of $totalBlocks")
        }

        override fun onError(download: Download, error: Error, throwable: Throwable?) {
            Timber.e(throwable, "error with download $download")
        }

        override fun onPaused(download: Download) {
            Timber.d("paused $download")
        }

        override fun onProgress(download: Download, etaInMilliSeconds: Long, downloadedBytesPerSecond: Long) {
            Timber.v("progress for $download, eta is $etaInMilliSeconds, rate is $downloadedBytesPerSecond")
        }

        override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
            Timber.d("queued $download")
            Timber.d("waiting on network $waitingOnNetwork")
        }

        override fun onRemoved(download: Download) {
            Timber.d("removed $download")
        }

        override fun onResumed(download: Download) {
            Timber.d("resumed $download")
        }

        override fun onStarted(download: Download, downloadBlocks: List<DownloadBlock>, totalBlocks: Int) {
            Timber.d("on started $download")
            Timber.d("blocks $downloadBlocks of $totalBlocks")
        }

        override fun onWaitingNetwork(download: Download) {
            Timber.d("$download is waiting on network")
        }
    }

    fun getVideoUrl(): String {
        Timber.v("download url [${download?.url}] quality url [${getQualityUrl(currentQuality)}]")
        if (download != null && download.url == getQualityUrl(currentQuality)) {
            val ret = Uri.fromFile(File(download.file)).toString()
            Timber.v("matched local download, file is [$ret]")
            return ret
        }

        currentUrl = getQualityUrl(currentQuality)
        return currentUrl ?: ""
    }

    fun setCurrentQuality(quality: VideoQuality) {
        this.currentQuality = quality
    }

    fun downloadVideo() {
//        rxFetch.enableLogging(true)
//        rxFetch.addListener(listener)
//        fetch.addListener(listener)
//        fetch.enableLogging(true)
//
        val file = File(getDownloadLocation(), getFileName(video))
        val request = Request(getVideoUrl(), file.absolutePath)
        request.priority = Priority.HIGH
        request.networkType = NetworkType.ALL
        request.enqueueAction = EnqueueAction.REPLACE_EXISTING
        request.tag = video.id.toString()
        Timber.d("$request")

//        val r = rxFetch.enqueue(request)
//        Timber.d("$r")
//        val flowable = rxFetch.getDownload(request.id).flowable.map { Optional.of(it) }.subscribeBy(
//                onNext = { Timber.d("onNext($it)") },
//                onError = { Timber.e("onError($it)") },
//                onComplete = { Timber.d("onComplete()") }
//        )

        fetch.enqueue(request)
        fetch.getDownload(request.id, Func2 { Timber.d("download $it") })
    }

    private fun getDownloadLocation(): String {
        // platform will return default value, not actually nullable
        val location = DownloadLocations.parse(prefs.getString(downloadLocationKey, DownloadLocations.Private.name))
        return when(location) {
            DownloadLocations.Private -> privateStorage
            DownloadLocations.Movies -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).path
            DownloadLocations.Downloads -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
            else -> throw LocalStorageDirectoryException("Could not get a path for a directory, app is in weird state")
        }
    }

    private fun getQualityUrl(quality: VideoQuality): String {
        val key = prefs.getString(API_KEY, "")
        val url = when (quality) {
            VideoQuality.Low -> video.lowUrl ?: ""
            VideoQuality.High -> video.highUrl ?: ""
            VideoQuality.HD -> video.hdUrl ?: ""
            VideoQuality.YouTube -> video.youtubeId ?: ""
        }
        return "$url?api_key=$key"
    }

    private fun getFileName(video: Video): String {
        return "${video.name?.replace(":", "")?.replace(" ", "")}_$currentQuality.mp4"
    }
}
