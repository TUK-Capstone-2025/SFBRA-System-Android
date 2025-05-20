package com.example.sfbra_system_android.ui.activities

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
                        // todo: UI 업데이트
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