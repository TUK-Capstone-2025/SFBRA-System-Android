package com.example.sfbra_system_android

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sfbra_system_android.databinding.ActivityRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // API 레벨 26 이상에서만 실행
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 시스템 내비게이션 바 버튼 색상 변경
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }

        binding.registerButton.setOnClickListener {
            register()
        }
    }

    // 회원가입 함수
    private fun register() {
        val loginId = binding.registerId.text.toString()
        val password = binding.registerPassword.text.toString()
        val nickname = binding.registerNickname.text.toString()
        val email = binding.registerEmail.text.toString()

        if (loginId.isEmpty() || password.isEmpty() || nickname.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val request = RegisterRequest(loginId, password, nickname, email)

        RetrofitClient.registerService.register(request).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    if (registerResponse != null && registerResponse.success) {
                        Toast.makeText(this@RegisterActivity, "회원가입 성공", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@RegisterActivity, "회원가입 실패", Toast.LENGTH_SHORT).show()
                        Log.d("Register", "회원가입 실패: ${registerResponse?.message}")
                    }
                } else {
                    Toast.makeText(this@RegisterActivity, "회원가입 실패: 서버 오류", Toast.LENGTH_SHORT).show()
                    Log.d("Register", "서버 오류: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "회원가입 실패: 네트워크 오류", Toast.LENGTH_SHORT).show()
                Log.d("Register", "네트워크 오류: ${t.message}")
            }
        })
    }
}