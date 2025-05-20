package com.example.sfbra_system_android.data.services

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface MemberProfileService {
    @GET("team/member/profile/{memberId}")
    fun getMemberProfile(@Path("memberId") memberId: Int): Call<MemberProfileResponse>
}

data class MemberProfileResponse(
    val success: Boolean,
    val message: String,
    val data: MemberProfile
)

data class MemberProfile(
    val nickname: String,
    val profileImageUrl: String?,
)