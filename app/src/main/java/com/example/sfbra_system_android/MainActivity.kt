package com.example.sfbra_system_android

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.sfbra_system_android.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // API 레벨 26 이상에서만 실행
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 시스템 내비게이션 바 버튼 색상 변경
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }

        toggle = ActionBarDrawerToggle(this, binding.drawer, R.string.drawer_open, R.string.drawer_close)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // 액션바 색깔 변경
        supportActionBar?.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.my_primary))

        toggle.syncState()

        // 좌측 사이드메뉴 클릭 리스너
        binding.mainDrawerView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.edit_profile -> true
                R.id.setting -> true
                else -> false
                }
        }

        // 기본 프래그먼트를 항상 설정 (앱 실행 시마다)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment()) // HomeFragment를 기본 프래그먼트로 설정
            .commit()

        // 하단 네비게이션에서 기본 선택 설정
        binding.bottomNavigation.selectedItemId = R.id.home

        // 하단 네비게이션 클릭 리스너
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.my_profile -> {
                    replaceFragment(MyProfileFragment())
                    true
                }
                R.id.riding_record -> {
                    replaceFragment(RidingPathFragment())
                    true
                }
                R.id.home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.my_team -> {
                    replaceFragment(MyTeamFragment())
                    true
                }
                R.id.bicycle_lock -> {
                    replaceFragment(BicycleLockFragment())
                    true
                }
                else -> false
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // 프래그먼트 교체 함수
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null) // 뒤로 가기 지원
            .commit()
    }
}