package com.example.sfbra_system_android

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.color
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.sfbra_system_android.databinding.ActivityMainBinding
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class MainActivity : AppCompatActivity() {
    private var doubleBackToExitPressedOnce = false
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "현재 위치: "

        printKeyHash(this)  // 로그에 키 해시 출력

        // 뒤로가기 동작 처리
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                } else {
                    if (doubleBackToExitPressedOnce) {
                        finish()
                    } else {
                        doubleBackToExitPressedOnce = true
                        Toast.makeText(this@MainActivity, "'뒤로' 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()

                        Handler(Looper.getMainLooper()).postDelayed({
                            doubleBackToExitPressedOnce = false
                        }, 3000)
                    }
                }
            }
        })

        // API 레벨 26 이상에서만 실행
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 시스템 내비게이션 바 버튼 색상 변경
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }

        toggle = ActionBarDrawerToggle(this, binding.drawer, R.string.drawer_open, R.string.drawer_close)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // 액션바 색깔 변경
        supportActionBar?.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.my_primary))
        // 액션바 글자색 변경
        val title = SpannableString(supportActionBar?.title ?: "")
        title.setSpan(ForegroundColorSpan(Color.WHITE), 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        supportActionBar?.title = title
        // 햄버거 버튼 색상 변경
        val arrowDrawable = toggle.drawerArrowDrawable
        arrowDrawable.color = ContextCompat.getColor(this, R.color.white)

        toggle.syncState()

        // 좌측 사이드메뉴 클릭 리스너
        binding.mainDrawerView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.edit_profile -> true
                R.id.setting -> {
                    startActivity(Intent(this, SettingActivity::class.java))
                    true
                }
                R.id.logout -> true
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
            .commit()
    }

    // 앱의 키 해시를 알기 위한 로그 출력 함수
    fun printKeyHash(context: android.content.Context) {
        try {
            val info = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val keyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT)
                Log.d("키해시:", keyHash)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
    }
}