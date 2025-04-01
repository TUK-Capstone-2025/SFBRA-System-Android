package com.example.sfbra_system_android.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sfbra_system_android.data.services.LoginRequest
import com.example.sfbra_system_android.data.services.LoginResponse
import com.example.sfbra_system_android.data.RetrofitClient
import com.example.sfbra_system_android.data.SharedPreferencesHelper
import com.example.sfbra_system_android.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// 로그인 화면
class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // API 레벨 26 이상에서만 실행
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 시스템 내비게이션 바 버튼 색상 변경
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }

        // 로그인 버튼 클릭 리스너
        binding.loginButton.setOnClickListener {
            login()
        }

        // 회원가입 버튼 클릭 리스너
        binding.registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    // 로그인 함수
    private fun login() {
        val loginId = binding.loginId.text.toString()
        val password = binding.loginPassword.text.toString()

        if (loginId.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val request = LoginRequest(loginId, password)

        val loginService = RetrofitClient.getLoginService()
        loginService.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null && loginResponse.success) { // 로그인 성공
                        // 로그인 성공하여 토큰 저장
                        Log.d("Token", "${loginResponse.data}")
                        SharedPreferencesHelper.saveToken(this@LoginActivity, loginResponse.data)

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent) // 메인 액티비티로 이동
                        finish() // 로그인 액티비티 종료
                    } else {
                        Toast.makeText(this@LoginActivity, "로그인 실패", Toast.LENGTH_SHORT).show()
                        Log.d("Login", "로그인 실패: ${loginResponse?.message}")
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "로그인 실패: 서버 오류", Toast.LENGTH_SHORT).show()
                    Log.d("Login", "서버 오류: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "로그인 실패: 네트워크 오류", Toast.LENGTH_SHORT).show()
                Log.d("Login", "네트워크 오류: ${t.message}")
            }
        })
    }
}