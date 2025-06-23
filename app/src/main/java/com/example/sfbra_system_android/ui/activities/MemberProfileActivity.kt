package com.example.sfbra_system_android.ui.activities

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.sfbra_system_android.R
import com.example.sfbra_system_android.data.RetrofitClient
import com.example.sfbra_system_android.data.SharedPreferencesHelper
import com.example.sfbra_system_android.data.services.MemberProfileResponse
import com.example.sfbra_system_android.databinding.ActivityMemberProfileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MemberProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityMemberProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemberProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // API 레벨 26 이상에서만 실행
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 시스템 내비게이션 바 버튼 색상 변경
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }

        setupActionBar()

        val memberId = intent.getIntExtra("memberId", -1)
        Log.d("PathViewActivity", "memberId: $memberId")
        if (memberId != -1) {
            getMemberProfile(memberId)
        } else {
            Log.d("PathViewActivity", "memberId가 유효하지 않습니다.")
            onBackPressedDispatcher.onBackPressed()
        }
    }

    // 액션바 색깔 수정 함수
    private fun setupActionBar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setBackgroundDrawable(ContextCompat.getDrawable(this@MemberProfileActivity, R.color.my_primary))

            val titleText = SpannableString("멤버 프로필")
            titleText.setSpan(ForegroundColorSpan(Color.WHITE), 0, titleText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            title = titleText

            // 뒤로가기 아이콘 색상 변경
            val upArrow = ContextCompat.getDrawable(this@MemberProfileActivity, R.drawable.ic_arrow_back)
            upArrow?.setTint(Color.WHITE)
            setHomeAsUpIndicator(upArrow)
        }

        // API 레벨 26 이상에서만 실행
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 시스템 내비게이션 바 버튼 색상 변경
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
    }

    // 뒤로가기
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed() // 또는 finish()(종료)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getMemberProfile(memberId: Int) {
        val token: String = SharedPreferencesHelper.getToken(application).toString()

        val memberProfileService = RetrofitClient.getMemberProfileService(token)
        memberProfileService.getMemberProfile(memberId).enqueue(object : Callback<MemberProfileResponse> {
            override fun onResponse(call: Call<MemberProfileResponse>, response: Response<MemberProfileResponse>) {
                if (response.isSuccessful) {
                    val memberProfileResponse = response.body()

                    if (memberProfileResponse != null && memberProfileResponse.success) {
                        val memberProfile = memberProfileResponse.data
                        val formatted = String.format("%.3f", memberProfile.totalDistance).trimEnd('0').trimEnd('.')
                        binding.distanceText.text = formatted
                        binding.nicknameText.text = memberProfile.nickname
                        val profileUrl = memberProfile.profileImageUrl
                        if (!profileUrl.isNullOrEmpty()) {
                            Glide.with(this@MemberProfileActivity)
                                .load(profileUrl)
                                .circleCrop()
                                .placeholder(R.drawable.ic_user)
                                .error(R.drawable.ic_user)
                                .into(binding.profileImage)
                        }
                    }
                    else {
                        Log.d("MemberProfileActivity","프로필 조회 실패: ${response.message()}")
                        Toast.makeText(this@MemberProfileActivity, "프로필 정보를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    Log.d("MemberProfileActivity","프로필 조회 실패: ${response.message()}")
                    Toast.makeText(this@MemberProfileActivity, "프로필 정보를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<MemberProfileResponse>, t: Throwable) {
                // 네트워크 오류
                Log.d("MemberProfileActivity","프로필 조회 실패: ${t.message}")
                Toast.makeText(this@MemberProfileActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}