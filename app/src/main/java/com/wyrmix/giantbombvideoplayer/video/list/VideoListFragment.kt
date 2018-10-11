package com.wyrmix.giantbombvideoplayer.video.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.wyrmix.giantbombvideoplayer.databinding.FragmentVideoListBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

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
        binding.recyclerView.adapter = VideoListAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        viewModel.posts.observe(this, Observer { pagedList ->

        })

        viewModel.showData()

//        GlobalScope.launch(Dispatchers.Main) {
//            val videos = viewModel.getVideos()
//            logger.info("Videos [${videos.results.size}]")
//
//            val shows = viewModel.getVideoShows()
//            logger.info("Shows [${shows.results}]")
//
//            val categories = viewModel.getVideoCategories()
//            logger.info("Categories [${categories.results}]")
//
//            val section = Section()
//            section.addAll(videos.results.map { it.toVideoItem() })
//            groupAdapter.add(section)
//
//            logger.info("Added ${section.itemCount} to adapter")
//        }

        return binding.root
    }
}
