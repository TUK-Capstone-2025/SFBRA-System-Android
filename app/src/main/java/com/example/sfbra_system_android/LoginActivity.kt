package com.example.sfbra_system_android

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sfbra_system_android.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val registerButton = binding.registerButton

        // 로그인 버튼 클릭 리스너
        binding.loginButton.setOnClickListener {
            login()
            finish()  // 로그인 액티비티 종료
        }

        // todo 회원가입 버튼 클릭 리스너
        registerButton.setOnClickListener {
            //val intent = Intent(this, RegisterActivity::class.java)
            //startActivity(intent)
            Toast.makeText(this, "회원가입 버튼 클릭", Toast.LENGTH_SHORT).show()
        }

        // API 레벨 26 이상에서만 실행
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 시스템 내비게이션 바 버튼 색상 변경
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
    }

    private fun login() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}