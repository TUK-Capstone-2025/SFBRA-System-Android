package com.example.sfbra_system_android.data

import retrofit2.Call
import retrofit2.http.GET

// 사용자 정보 API 호출 인터페이스
interface ProfileService {
    @GET("member/me") // 엔드포인트
    fun getUserInfo(): Call<ProfileResponse>
}

// 사용자 정보 응답 데이터 클래스
data class ProfileResponse(
    val success: Boolean,
    val message: String,
    val data: UserData
)

// 사용자 데이터 형식
data class UserData(
    val name: String,
    val nickname: String,
    val userId: String
)