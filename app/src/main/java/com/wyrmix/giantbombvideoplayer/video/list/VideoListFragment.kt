package com.wyrmix.giantbombvideoplayer.video.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
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

//        groupAdapter.setOnItemClickListener { item, view ->
//            run {
//                when (item) {
//                    is VideoItem -> {
//                        val video = item.video
//                        val action = VideoListFragmentDirections.actionVideoListFragmentToVideoDetailsFragment(video)
//                        view.findNavController().navigate(action)
//                    }
//                }
//            }
//        }

        binding.swipeToRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
        val adapter = VideoListAdapter()
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
