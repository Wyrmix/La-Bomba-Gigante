package com.wyrmix.giantbombvideoplayer.video.list

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.wyrmix.giantbombvideoplayer.R
import com.wyrmix.giantbombvideoplayer.video.database.Video

class VideoListAdapter: PagedListAdapter<Video, RecyclerView.ViewHolder>(POST_COMPARATOR) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.video_card -> VideoViewHolder.create(parent)
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is VideoViewHolder -> holder.bind(getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.video_card
    }

    companion object {
        val POST_COMPARATOR = object : DiffUtil.ItemCallback<Video>() {
            override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean =
                    oldItem == newItem

            override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean =
                    oldItem.name == newItem.name
        }
    }
}