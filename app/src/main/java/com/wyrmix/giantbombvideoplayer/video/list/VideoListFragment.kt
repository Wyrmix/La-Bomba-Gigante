package com.wyrmix.giantbombvideoplayer.video.list

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.util.LruCache
import com.google.android.material.chip.Chip
import com.wyrmix.giantbombvideoplayer.R
import com.wyrmix.giantbombvideoplayer.databinding.FragmentVideoListBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class VideoListFragment: Fragment() {
    private val viewModel by sharedViewModel<VideoBrowseViewModel>()
    val memCache by inject<LruCache<String, Bitmap>>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentVideoListBinding.inflate(inflater, container, false)

        Timber.i("viewModel $viewModel")
        binding.swipeToRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
        val adapter = VideoListAdapter { video, view ->
            video?.apply {
                val extras = FragmentNavigatorExtras(view.findViewById<AppCompatImageView>(R.id.video_thumbnail) to view.transitionName)
                val action = VideoListFragmentDirections.actionVideoListFragmentToVideoDetailsFragment(video, null)
                TransitionInflater.from(context).inflateTransition(R.transition.change_bounds).addTarget(view.findViewById<AppCompatImageView>(R.id.video_thumbnail))
                binding.root.findNavController().navigate(action, extras)
            }
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        viewModel.posts.observe(this, Observer { pagedList ->
            adapter.submitList(pagedList)
        })

        viewModel.videoTypes.observe(this@VideoListFragment, Observer { videoType ->
            videoType.forEach {
                val chip = Chip(context)
                chip.text = it.title
                chip.isCheckable = true

                memCache.get(it.title)?.apply {
                    chip.chipIcon = BitmapDrawable(resources, this)
                }

                chip.tag = it
                binding.chipGroup.addView(chip)
            }
        })

        viewModel.showData()

        return binding.root
    }
}
