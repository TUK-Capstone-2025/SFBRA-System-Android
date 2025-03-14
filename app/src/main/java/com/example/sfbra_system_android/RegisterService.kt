package com.example.sfbra_system_android

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

// Retrofit을 사용하기 위한(회원가입 api호출을 위한) 인터페이스와 데이터 클래스 정의

// 회원가입 요청 데이터 클래스
data class RegisterRequest(
    val loginId: String,
    val password: String,
    val nickname: String,
    val email: String
)

// 회원가입 응답 데이터 클래스
data class RegisterResponse(
    val success: Boolean,
    val message: String
)

interface RegisterService {
    @POST("api/member/register") // 회원가입 API 엔드포인트 경로
    fun register(@Body request: RegisterRequest): Call<RegisterResponse> // 회원가입 요청 메서드(회원가입 정보들 JSON 형식으로 전송)

}