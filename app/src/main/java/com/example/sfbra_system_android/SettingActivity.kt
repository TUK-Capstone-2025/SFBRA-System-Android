package com.example.sfbra_system_android

import android.os.Build
import android.os.Bundle
import android.preference.Preference
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.preference.PreferenceFragmentCompat

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        title = "설정"

        // API 레벨 26 이상에서만 실행
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 시스템 내비게이션 바 버튼 색상 변경
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)  // 액션바 뒤로가기 버튼 활성화
        }

        supportFragmentManager
            .beginTransaction()
            .replace(android.R.id.content, SettingFragment())
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish() // 뒤로가기 버튼 클릭 시 액티비티 종료
        return true
    }
}