package com.example.sfbra_system_android.data.services

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

// 사용자 프로필 변경 api 호출 인터페이스
interface ProfileUpdateService {
    @POST("member/changeNick") // 닉네임 변경 엔드포인트
    fun changeNickname(@Body request: ChangeNicknameRequest): Call<ChangeNicknameResponse>

    @POST("member/changeId") // 아이디 변경 엔드포인트
    fun changeUserId(@Body request: ChangeUserIdRequest): Call<ChangeUserIdResponse>

    @POST("member/changePass") // 비밀번호 변경 엔드포인트
    fun changePassword(@Body request: ChangePasswordRequest): Call<ChangePasswordResponse>

}

// 프로필 변경 요청 데이터 클래스
data class ChangeNicknameRequest(val newNickname: String)
data class ChangeUserIdRequest(val newUserId: String)
data class ChangePasswordRequest(val currentPassword: String, val newPassword: String)

// 프로필 변경 응답 데이터 클래스
//data class ApiResponse(val success: Boolean, val message: String, val data: String)
data class ChangeNicknameResponse(val success: Boolean, val message: String, val data: String)
data class ChangeUserIdResponse(val success: Boolean, val message: String, val data: String)
data class ChangePasswordResponse(val success: Boolean, val message: String, val data: String)