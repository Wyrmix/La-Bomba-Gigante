package com.wyrmix.giantbombvideoplayer.video.list

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
import com.wyrmix.giantbombvideoplayer.R
import com.wyrmix.giantbombvideoplayer.databinding.FragmentVideoListBinding
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.IO
import kotlinx.coroutines.experimental.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class VideoListFragment: Fragment() {
    private val viewModel by viewModel<VideoBrowserViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentVideoListBinding.inflate(inflater, container, false)

        binding.swipeToRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
        val adapter = VideoListAdapter { video, view ->
            video?.apply {
                val extras = FragmentNavigatorExtras(view.findViewById<AppCompatImageView>(R.id.video_thumbnail) to view.transitionName)
                val action = VideoListFragmentDirections.actionVideoListFragmentToVideoDetailsFragment(video)
                TransitionInflater.from(context).inflateTransition(R.transition.change_bounds).addTarget(view.findViewById<AppCompatImageView>(R.id.video_thumbnail))
                binding.root.findNavController().navigate(action, extras)
            }
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        viewModel.posts.observe(this, Observer { pagedList ->
            adapter.submitList(pagedList)
        })

        viewModel.showData()

        GlobalScope.launch(Dispatchers.IO) {
            val videos = viewModel.getVideos()
            Timber.i("Videos [${videos.results.size}]")

            val shows = viewModel.getVideoShows()
            Timber.i("Shows [${shows.results.size}]")

            val categories = viewModel.getVideoCategories()
            Timber.i("Categories [${categories.results.size}]")
        }

        return binding.root
    }
}
