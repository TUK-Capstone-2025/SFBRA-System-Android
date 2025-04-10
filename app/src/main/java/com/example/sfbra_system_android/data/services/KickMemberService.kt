package com.example.sfbra_system_android.data.services

import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Path

interface KickMemberService {
    @POST("team/kick/{memberId}")
    fun kickMember(@Path("memberId") memberId: Int): Call<KickMemberResponse>
}

data class KickMemberResponse(
    val success: Boolean,
    val message: String,
    val data: String?
)