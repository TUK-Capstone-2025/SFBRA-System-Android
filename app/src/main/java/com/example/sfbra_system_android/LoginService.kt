package com.example.sfbra_system_android

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

// Retrofit을 사용하기 위한(로그인 api호출을 위한) 인터페이스와 데이터 클래스 정의

// 로그인 요청 데이터 클래스
data class LoginRequest(val loginId: String, val password: String)

// 로그인 응답 데이터 클래스
data class LoginResponse(val success: Boolean, val message: String, val token: String)

//data class RegisterRequest(val loginId: String, val password: String, val name: String, val phoneNumber: String)

interface LoginService {
    @POST("api/member/login") // 로그인 API 엔드포인트 경로
    fun login(@Body request: LoginRequest): Call<LoginResponse> // 로그인 요청 메서드(id, pw를 JSON 형식으로 전송)
}