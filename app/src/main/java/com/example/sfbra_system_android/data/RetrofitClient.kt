package com.example.sfbra_system_android.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// 서버 통신을 위한 Retrofit 클라이언트 객체 생성
object RetrofitClient {
    // todo : 서버 주소 입력
    private  const val BASE_URL = "https://99e0-210-99-254-13.ngrok-free.app/api/" // base 주소

    // 로그인 API
    val loginService: LoginService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // JSON 변환기 추가
            .build()
            .create(LoginService::class.java)
    }

    // 회원가입 API
    val registerService: RegisterService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // JSON 변환기 추가
            .build()
            .create(RegisterService::class.java)
    }
}