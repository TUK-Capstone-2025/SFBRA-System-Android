package com.example.sfbra_system_android.ui.fragments

import android.os.Bundle
import android.text.InputType
import android.text.method.DigitsKeyListener
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

        // 숫자 전용 키패드 + 0~9만 입력 허용
        editPref?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            editText.keyListener = DigitsKeyListener.getInstance("0123456789")
        }

        // 선택값 사용자 지정일 때만 활성화
        listPref?.setOnPreferenceChangeListener { _, newValue ->
            editPref?.isEnabled = (newValue == "custom")
            true
        }

        // 처음 진입 시도 반영
        editPref?.isEnabled = (listPref?.value == "custom")
    }
}