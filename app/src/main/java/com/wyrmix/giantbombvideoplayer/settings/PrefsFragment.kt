package com.wyrmix.giantbombvideoplayer.settings

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import com.wyrmix.giantbombvideoplayer.R

class PrefsFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        findPreference(getString(R.string.pref_key_settings_account_premium)).setOnPreferenceClickListener { pref ->
            findNavController().navigate(PrefsFragmentDirections.actionSettingsFragmentToAuthFragment())
            true
        }
    }
}