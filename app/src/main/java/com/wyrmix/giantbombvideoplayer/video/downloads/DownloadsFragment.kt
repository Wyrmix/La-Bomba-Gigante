package com.wyrmix.giantbombvideoplayer.video.downloads

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
import com.wyrmix.giantbombvideoplayer.databinding.FragmentDownloadsBinding
import io.reactivex.rxkotlin.subscribeBy
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class DownloadsFragment: Fragment() {
    private val viewModel by viewModel<DownloadsViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentDownloadsBinding.inflate(inflater, container, false)
        binding.swipeToRefresh.setOnRefreshListener {
            viewModel.refresh().subscribeBy(
                    onNext = { Timber.d("onNext($it)") },
                    onError = { Timber.e("onError($it)") },
                    onComplete = { Timber.d("onComplete()") }
            )
        }
        val adapter = DownloadsAdapter { videoDownload, view ->
            videoDownload?.apply {
                val extras = FragmentNavigatorExtras(view.findViewById<AppCompatImageView>(R.id.video_thumbnail) to view.transitionName)
                val action = DownloadsFragmentDirections.actionDownloadsFragmentToVideoDetailsFragment(videoDownload.video, videoDownload.download)
                TransitionInflater.from(context).inflateTransition(R.transition.change_bounds).addTarget(view.findViewById<AppCompatImageView>(R.id.video_thumbnail))
                binding.root.findNavController().navigate(action, extras)
            }
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.logDownloadProgress()
        viewModel.getDownloads()

        viewModel.downloads().observe(this, Observer { downloads ->
            Timber.d("videoDownloads [$downloads]")
            adapter.addDownloads(downloads)
        })

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        viewModel.dispose()
    }
}