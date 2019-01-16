package com.wyrmix.giantbombvideoplayer.video.details

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.wyrmix.giantbombvideoplayer.R
import com.wyrmix.giantbombvideoplayer.databinding.FragmentVideoDetailsBinding
import com.wyrmix.giantbombvideoplayer.video.downloads.VideoDownload
import com.wyrmix.giantbombvideoplayer.video.player.VideoActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import timber.log.Timber

class VideoDetailsFragment: Fragment() {
    val videoDetailsViewModel by inject<VideoDetailsViewModel> {
        val args = VideoDetailsFragmentArgs.fromBundle(arguments!!)
        parametersOf(VideoDownload(args.video, args.download))
    }

    private val EXTERNAL_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(R.transition.change_bounds)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentVideoDetailsBinding.inflate(inflater, container, false)
        binding.viewModel = videoDetailsViewModel
        Glide.with(context!!).load(videoDetailsViewModel.video.videoImage.screenUrl).into(binding.detailImage)

        val permissionArray = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ActivityCompat.requestPermissions(activity!!, permissionArray, EXTERNAL_REQUEST_CODE)
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
        }

        binding.fab.setOnClickListener {
            val intent = Intent(context, VideoActivity::class.java)
            val url = videoDetailsViewModel.getVideoUrl()
            Timber.d("Playing video at address [$url]")
            intent.putExtra(VideoActivity.ARG_VIDEO_URL, url)
            startActivity(intent)
        }

        binding.qualitySpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Timber.w("nothing selected, no idea how the user did this lmao")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val quality = VideoQuality.fromInt(id.toInt()) ?: VideoQuality.High
                Timber.d("selected quality is $quality")
                videoDetailsViewModel.setCurrentQuality(quality)
                binding.downloadButton.isEnabled = quality != VideoQuality.YouTube
            }
        }

        return binding.root
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            EXTERNAL_REQUEST_CODE -> {
                val isExternalPermissionGranted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                when (isExternalPermissionGranted) {
                    true -> {
                        Timber.d("Permission granted for external storage")
//                        downloadTestFile()
                    }
                    false -> Timber.d("Permission denied for external storage")
                }
            }
        }
    }
}