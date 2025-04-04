package com.example.sfbra_system_android.ui.fragments

import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.example.sfbra_system_android.R

class SettingFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        // 사용자 지정 번호 사용 시 동적 활성화
        val listPref = findPreference<ListPreference>("emergency_number_choice")
        val editPref = findPreference<EditTextPreference>("custom_emergency_number")

        listPref?.setOnPreferenceChangeListener { _, newValue ->
            editPref?.isEnabled = (newValue == "custom")
            true
        }

        // 처음 진입 시도 반영
        editPref?.isEnabled = (listPref?.value == "custom")
    }
}