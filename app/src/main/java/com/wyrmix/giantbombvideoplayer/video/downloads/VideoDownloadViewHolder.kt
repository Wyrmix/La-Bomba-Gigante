package com.wyrmix.giantbombvideoplayer.video.downloads

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wyrmix.giantbombvideoplayer.databinding.VideoCardBinding
import timber.log.Timber

/**
 * Created by kylea
 *
 * 1/11/2019 at 6:08 PM
 */
class VideoDownloadViewHolder(val binding: VideoCardBinding, view: View): RecyclerView.ViewHolder(view) {

    fun bind(videoDownloads: VideoDownload?, navigate: (videoDownload: VideoDownload?, view: View) -> Unit) {
        binding.video = videoDownloads?.video
        binding.download = videoDownloads?.download
        binding.clickListener = View.OnClickListener {
            Timber.v("clicking on video [${videoDownloads?.video?.id}]")
            navigate.invoke(videoDownloads, binding.root)
        }
    }

    companion object {
        fun create(parent: ViewGroup): VideoDownloadViewHolder {
            val binding = VideoCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return VideoDownloadViewHolder(binding, binding.root)
        }
    }
}