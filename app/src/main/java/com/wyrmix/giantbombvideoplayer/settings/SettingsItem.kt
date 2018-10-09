package com.wyrmix.giantbombvideoplayer.settings

import com.wyrmix.giantbombvideoplayer.R
import com.wyrmix.giantbombvideoplayer.databinding.ItemSettingsBinding
import com.xwray.groupie.databinding.BindableItem

class SettingsItem(val setting: Setting): BindableItem<ItemSettingsBinding>() {
    override fun getLayout(): Int = R.layout.item_settings

    override fun bind(binding: ItemSettingsBinding, position: Int) {
        binding.setting = setting
    }
}