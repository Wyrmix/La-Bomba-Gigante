package com.wyrmix.giantbombvideoplayer.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.wyrmix.giantbombvideoplayer.R
import com.wyrmix.giantbombvideoplayer.base.HeaderItem
import com.wyrmix.giantbombvideoplayer.base.HeaderItemDecoration
import com.wyrmix.giantbombvideoplayer.databinding.FragmentSettingsBinding
import com.wyrmix.giantbombvideoplayer.extension.GroupieAdapter
import com.xwray.groupie.Section
import timber.log.Timber

class SettingsFragment: Fragment() {
    private val white: Int by lazy { ContextCompat.getColor(context!!, R.color.white) }
    private val betweenPadding: Int by lazy { resources.getDimensionPixelSize(R.dimen.padding_small) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentSettingsBinding.inflate(inflater, container, false)

        val groupAdapter = GroupieAdapter()
        groupAdapter.setOnItemClickListener { item, _ ->
            run {
                when (item) {
                    is HeaderItem -> {
                        Timber.d("clicked header [$item]")
                    }
                    is SettingsItem -> {
                        Timber.d("Settings fragment [${item.setting}]")
                    }
                }
            }
        }

        binding.recyclerView.adapter = groupAdapter
        binding.recyclerView.layoutManager = GridLayoutManager(context, groupAdapter.spanCount)

        val accountSection = Section()
        accountSection.setHeader(HeaderItem(R.string.settings_section_account, R.string.settings_description_account))
        accountSection.add(SettingsItem(Setting("Premium Account", "Launch authentication flow")))
        accountSection.add(SettingsItem(Setting("Sync Playback Position", "Sync playback position to your account")))
        groupAdapter.add(accountSection)

        val appearanceSection = Section()
        appearanceSection.setHeader(HeaderItem(R.string.settings_section_appearance, R.string.settings_description_appearance))
        appearanceSection.add(SettingsItem(Setting("Dark Mode", "Make the app dark, yo")))
        groupAdapter.add(appearanceSection)

        val notificationSettings = Section()
        notificationSettings.setHeader(HeaderItem(R.string.settings_section_notifications, R.string.settings_description_notifications))
        notificationSettings.add(SettingsItem(Setting("Enable notifications", "Go to system notification settings")))
        groupAdapter.add(notificationSettings)

        val videoSection = Section()
        videoSection.setHeader(HeaderItem(R.string.settings_section_video, R.string.settings_description_video))
        videoSection.add(SettingsItem(Setting("Picture in Picture", "Enable or disable PiP")))
        videoSection.add(SettingsItem(Setting("Default Quality", "The default quality setting for video playback")))
        groupAdapter.add(videoSection)

        val downloadSection = Section()
        downloadSection.setHeader(HeaderItem(R.string.settings_section_download, R.string.settings_description_download))
        downloadSection.add(SettingsItem(Setting("Disk size limit", "Set limits for how much space the app will use")))
        groupAdapter.add(downloadSection)

        binding.recyclerView.addItemDecoration(HeaderItemDecoration(white, betweenPadding, R.layout.item_header))

        return binding.root
    }
}
