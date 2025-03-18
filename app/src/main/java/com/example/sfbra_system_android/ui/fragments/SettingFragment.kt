package com.example.sfbra_system_android.ui.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.sfbra_system_android.R

class SettingFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}