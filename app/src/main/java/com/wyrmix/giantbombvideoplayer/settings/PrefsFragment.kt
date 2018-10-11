package com.wyrmix.giantbombvideoplayer.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.wyrmix.giantbombvideoplayer.R

class PrefsFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}