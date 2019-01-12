package com.wyrmix.giantbombvideoplayer.video.downloads

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wyrmix.giantbombvideoplayer.R
import timber.log.Timber

/**
 * Created by kylea
 *
 * 1/11/2019 at 6:00 PM
 */
class DownloadsAdapter(val navigate: (videoDownload: VideoDownload?, view: View) -> Unit): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val list: MutableList<VideoDownload> = mutableListOf()

    fun addDownloads(downloadsList: List<VideoDownload>) {
        val lastItem = list.size -1
        list.addAll(downloadsList)
        Timber.d("DownloadsAdapter [$list]")
        notifyItemRangeInserted(lastItem, downloadsList.size)
    }

    fun setDownloads(downloadsList: List<VideoDownload>) {
        list.clear()
        list.addAll(downloadsList)
        Timber.d("DownloadsAdapter [$list]")
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Timber.v("onCreateViewHolder($parent: ViewGroup, $viewType: Int)")
        return when (viewType) {
            R.layout.video_card -> VideoDownloadViewHolder.create(parent)
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.video_card
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Timber.v("onBindViewHolder($holder: RecyclerView.ViewHolder, $position: Int)")
        when (holder) {
            is VideoDownloadViewHolder -> holder.bind(list[position], navigate)
        }
    }
}