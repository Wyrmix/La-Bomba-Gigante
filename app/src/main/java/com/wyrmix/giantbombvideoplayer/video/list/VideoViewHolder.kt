package com.wyrmix.giantbombvideoplayer.video.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wyrmix.giantbombvideoplayer.databinding.VideoCardBinding
import com.wyrmix.giantbombvideoplayer.video.database.Video
import timber.log.Timber

class VideoViewHolder(val binding: VideoCardBinding, view: View): RecyclerView.ViewHolder(view) {

    fun bind(video: Video?, navigate: (video: Video?, view: View) -> Unit) {
        binding.video = video
        binding.clickListener = View.OnClickListener {
            Timber.v("clicking on video [${video?.id}]")
            navigate.invoke(video, binding.root)
        }
    }

    companion object {
        fun create(parent: ViewGroup): VideoViewHolder {
            val binding = VideoCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return VideoViewHolder(binding, binding.root)
        }
    }
}