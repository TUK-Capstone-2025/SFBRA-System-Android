package com.example.sfbra_system_android.data

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class ChangeNicknameRequest(val newNickname: String)
data class ChangeUserIdRequest(val newUserId: String)
data class ChangePasswordRequest(val currentPassword: String, val newPassword: String)

//data class ApiResponse(val success: Boolean, val message: String, val data: String)
data class ChangeNicknameResponse(val success: Boolean, val message: String, val data: String)
data class ChangeUserIdResponse(val success: Boolean, val message: String, val data: String)
data class ChangePasswordResponse(val success: Boolean, val message: String, val data: String)

interface ProfileUpdateService {
    @POST("member/changeNick")
    fun changeNickname(@Body request: ChangeNicknameRequest): Call<ChangeNicknameResponse>

    @POST("member/changeId")
    fun changeUserId(@Body request: ChangeUserIdRequest): Call<ChangeUserIdResponse>

    @POST("member/changePass")
    fun changePassword(@Body request: ChangePasswordRequest): Call<ChangePasswordResponse>

}