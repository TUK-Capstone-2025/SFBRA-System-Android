package com.example.sfbra_system_android

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

// Retrofit을 사용하기 위한 인터페이스와 데이터 클래스 정의(api들)

data class LoginRequest(val loginId: String, val password: String)

data class LoginResponse(val success: Boolean, val message: String, val token: String)

//data class RegisterRequest(val loginId: String, val password: String, val name: String, val phoneNumber: String)

interface LoginService {
    @POST("api/member/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}