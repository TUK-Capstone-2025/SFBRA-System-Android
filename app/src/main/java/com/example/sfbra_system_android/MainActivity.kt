package com.example.sfbra_system_android

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
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
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.sfbra_system_android.databinding.ActivityMainBinding
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class MainActivity : AppCompatActivity() {
    private var doubleBackToExitPressedOnce = false
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var binding: ActivityMainBinding

    // 프래그먼트 저장용 맵
    private val fragmentMap = mutableMapOf<String, Fragment>()
    private var activeFragment: Fragment? = null

    // 블루투스 연결 확인용 변수
    var isBluetoothConnected: Boolean = false
        private set // 외부에서 직접 변경하지 못하도록 private 설정

    var isBicycleLock: Boolean = false
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "현재 위치: "

        printKeyHash(this)  // 로그에 키 해시 출력

        setupBackPressedHandler()
        setupNavigationDrawer()
        setupBottomNavigation()

        // 첫 화면: 홈 프래그먼트
        switchFragment(HomeFragment(), "HOME")
        binding.bottomNavigation.selectedItemId = R.id.home
    }

    // 뒤로가기 두번 눌렀을 시 종료
    private fun setupBackPressedHandler() {
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
    }

    // 액션바 색깔 수정 함수
    private fun setupActionBar() {
        toggle = ActionBarDrawerToggle(this, binding.drawer, R.string.drawer_open, R.string.drawer_close)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setBackgroundDrawable(ContextCompat.getDrawable(this@MainActivity, R.color.my_primary))

            val titleText = SpannableString(title ?: "")
            titleText.setSpan(ForegroundColorSpan(Color.WHITE), 0, titleText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            title = titleText
        }

        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, R.color.white)
        toggle.syncState()

        // API 레벨 26 이상에서만 실행
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 시스템 내비게이션 바 버튼 색상 변경
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
    }

    // 사이드 메뉴 설정 함수
    private fun setupNavigationDrawer() {
        binding.mainDrawerView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.edit_profile -> true
                R.id.setting -> {
                    startActivity(Intent(this, SettingActivity::class.java))
                    true
                }
                R.id.logout -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    // 네비게이션 바 버튼 설정 함수
    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.my_profile -> switchFragment(MyProfileFragment(), "MY_PROFILE")
                R.id.riding_record -> switchFragment(RidingPathFragment(), "RIDING_RECORD")
                R.id.home -> switchFragment(HomeFragment(), "HOME")
                R.id.my_team -> switchFragment(MyTeamFragment(), "MY_TEAM")
                R.id.bicycle_lock -> switchFragment(BicycleLockFragment(), "BICYCLE_LOCK")
                else -> false
            }
            true
        }
    }

    // 프래그먼트 교체 함수
    private fun switchFragment(fragment: Fragment, tag: String) {
        if (activeFragment?.tag == tag) return

        val transaction = supportFragmentManager.beginTransaction()

        // 기존 프래그먼트 숨기기
        activeFragment?.let { transaction.hide(it) }

        // 새 프래그먼트 가져오기 (기존 프래그먼트가 있으면 재사용)
        val newFragment = fragmentMap[tag] ?: fragment.also { fragmentMap[tag] = it }

        // 프래그먼트 추가 또는 보여주기
        if (!newFragment.isAdded) {
            transaction.add(R.id.fragment_container, newFragment, tag)
        } else {
            transaction.show(newFragment)
        }

        transaction.commit()
        activeFragment = newFragment
    }

    // 블루투스 상태 업데이트 함수
    fun setBluetoothConnectionState(isConnected: Boolean) {
        isBluetoothConnected = isConnected
    }

    // 잠금 상태 업데이트 함수
    fun setBicycleLockState(isLock: Boolean) {
        isBicycleLock = isLock
    }

    // 현재 지도 위치로 액션바 타이틀 변경 함수
    fun setTitleFromLocation(location: String) {
        Log.d("Location in main", "$location")
        val shortAddress = simplifyAddress(location) // 지명 줄이기
        supportActionBar?.title = "현재 위치: $shortAddress"

        setupActionBar()
    }

    // 지명 줄이기 함수
    private fun simplifyAddress(address: String): String {
        return address
            .replace("대한민국 ", "") // "대한민국 " 삭제
            .substringAfter("시 ")   // "~시"까지 생략하고 그 뒤부터 표시
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) true else super.onOptionsItemSelected(item)
    }

    // 키 해시 출력 함수
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
